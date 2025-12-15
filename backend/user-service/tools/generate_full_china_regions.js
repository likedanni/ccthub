#!/usr/bin/env node
const https = require("https");
const fs = require("fs");
const path = require("path");

const url =
  "https://raw.githubusercontent.com/modood/Administrative-divisions-of-China/master/dist/pcas-code.json";

function fetchJson(url) {
  const localFile = path.join(__dirname, "pcas-code.json");
  if (fs.existsSync(localFile)) {
    try {
      const text = fs.readFileSync(localFile, "utf8");
      return Promise.resolve(JSON.parse(text));
    } catch (e) {
      return Promise.reject(e);
    }
  }

  const maxRetries = 3;
  const timeoutMs = 15000;

  return new Promise((resolve, reject) => {
    let attempts = 0;
    function attempt() {
      attempts += 1;
      const req = https.get(
        url,
        { headers: { "User-Agent": "node.js" } },
        (res) => {
          if (res.statusCode !== 200) {
            req.destroy();
            return onError(
              new Error("Failed to fetch JSON: " + res.statusCode)
            );
          }
          let data = "";
          res.on("data", (chunk) => (data += chunk));
          res.on("end", () => {
            try {
              const json = JSON.parse(data);
              resolve(json);
            } catch (e) {
              onError(e);
            }
          });
        }
      );
      req.on("error", onError);
      req.setTimeout(timeoutMs, () => {
        req.destroy(new Error("Timeout"));
      });
    }

    function onError(err) {
      if (attempts < maxRetries) {
        console.warn(
          `Fetch attempt ${attempts} failed: ${err.message}, retrying...`
        );
        setTimeout(attempt, 2000);
        return;
      }
      reject(err);
    }

    attempt();
  });
}

function normalizeCode(code) {
  // Normalize to the 6-digit scheme used in existing migrations:
  // - province: 2-digit -> append 4 zeros (e.g. 62 -> 620000)
  // - city: 4-digit -> append 2 zeros (e.g. 6201 -> 620100)
  // - district: 6-digit -> keep as-is
  if (code === undefined || code === null) return null;
  const s = String(code).trim();
  if (s.length === 2) return s + "0000";
  if (s.length === 4) return s + "00";
  if (s.length >= 6) return s.slice(0, 6);
  return s.padStart(6, "0");
}

async function main() {
  console.log("Fetching administrative divisions JSON...", url);
  const data = await fetchJson(url);
  if (!data) throw new Error("Fetched empty payload");
  // `pcas-code.json` is expected to be an array of province objects
  const provinces = Array.isArray(data)
    ? data
    : data.provinces || data.items || [];
  console.log("Provinces entries count:", provinces.length);

  const inserts = [];

  // provinces
  for (const prov of provinces) {
    try {
      const pcodeRaw = prov.code || prov.value || prov.Id || prov.id;
      const pcode = normalizeCode(pcodeRaw);
      const pname = String(
        prov.name || prov.label || prov.text || prov.title || prov["name"] || ""
      ).replace(/'/g, "''");
      if (!pcode) {
        console.warn("Skipping province with missing code:", pname);
        continue;
      }
      inserts.push(`('${pcode}','${pname}',NULL,1)`);

      const cities = Array.isArray(prov.children)
        ? prov.children
        : prov.city || [];
      for (const city of cities) {
        try {
          const ccodeRaw = city.code || city.value || city.Id || city.id;
          const ccode = normalizeCode(ccodeRaw);
          const cname = String(
            city.name || city.label || city.text || ""
          ).replace(/'/g, "''");
          if (!ccode) {
            console.warn(
              `Skipping city with missing code under ${pname}:`,
              cname
            );
            continue;
          }
          inserts.push(`('${ccode}','${cname}','${pcode}',2)`);

          const districts = Array.isArray(city.children)
            ? city.children
            : city.district || [];
          for (const dist of districts) {
            try {
              const dcodeRaw = dist.code || dist.value || dist.Id || dist.id;
              const dcode = normalizeCode(dcodeRaw);
              const dname = String(
                dist.name || dist.label || dist.text || ""
              ).replace(/'/g, "''");
              if (!dcode) {
                console.warn(
                  `Skipping district with missing code under ${cname}:`,
                  dname
                );
                continue;
              }
              inserts.push(`('${dcode}','${dname}','${ccode}',3)`);
            } catch (e) {
              console.warn("Error processing district", e && e.message);
            }
          }
        } catch (e) {
          console.warn("Error processing city", e && e.message);
        }
      }
    } catch (e) {
      console.warn("Error processing province", e && e.message);
    }
  }

  // Split into batches to avoid extremely long single INSERT
  const batchSize = 500;
  const lines = [];
  for (let i = 0; i < inserts.length; i += batchSize) {
    const batch = inserts.slice(i, i + batchSize);
    lines.push(
      `INSERT IGNORE INTO china_regions (code, name, parent_code, level) VALUES ${batch.join(
        ","
      )};`
    );
  }

  const outDir = path.join(
    __dirname,
    "..",
    "src",
    "main",
    "resources",
    "db",
    "migration"
  );
  if (!fs.existsSync(outDir)) fs.mkdirSync(outDir, { recursive: true });
  const outFile = path.join(outDir, "V10__china_full_regions.sql");
  fs.writeFileSync(outFile, lines.join("\n") + "\n", "utf8");
  console.log("Wrote", outFile, "with", inserts.length, "rows.");
}

main().catch((err) => {
  console.error(err);
  process.exit(1);
});
