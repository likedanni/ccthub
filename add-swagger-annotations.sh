#!/bin/bash

# 为所有控制器添加 Swagger 注解的脚本

BACKEND_PATH="/Users/like/CCTHub/backend/user-service/src/main/java/com/ccthub/userservice/controller"

echo "开始为控制器添加 Swagger 注解..."

# RefundController
echo "处理 RefundController..."
perl -i -pe '
BEGIN { $done = 0; }
if (!$done && /^package com\.ccthub/) {
    $_ .= "\nimport io.swagger.v3.oas.annotations.Operation;\nimport io.swagger.v3.oas.annotations.Parameter;\nimport io.swagger.v3.oas.annotations.tags.Tag;\n";
}
if (!$done && /@RestController/) {
    $_ = "\@Tag(name = \"退款管理\", description = \"退款相关接口，包括退款申请、退款审核、退款处理和退款查询等功能\")\n" . $_;
    $done = 1;
}
s/^(\s*)@PostMapping\s*$/\$1@Operation(summary = "创建退款申请", description = "用户申请退款，创建退款记录")\n\$1@PostMapping/;
s/public ResponseEntity<Map<String, Object>> createRefund\(@Valid @RequestBody RefundRequest request\)/public ResponseEntity<Map<String, Object>> createRefund(\n            @Parameter(description = "退款申请信息", required = true) @Valid @RequestBody RefundRequest request)/;
s/^(\s*)@PutMapping\("\/audit"\)\s*$/\$1@Operation(summary = "审核退款申请", description = "管理员审核用户的退款申请")\n\$1@PutMapping("\/audit")/;
s/public ResponseEntity<Map<String, Object>> auditRefund\(\s*@Valid @RequestBody RefundAuditRequest request\)/public ResponseEntity<Map<String, Object>> auditRefund(\n            @Parameter(description = "退款审核信息", required = true) @Valid @RequestBody RefundAuditRequest request)/;
s/^(\s*)@PostMapping\("\/{refundNo}\/process"\)\s*$/\$1@Operation(summary = "处理退款", description = "执行退款操作，将款项退还给用户")\n\$1@PostMapping("\/{refundNo}\/process")/;
s/public ResponseEntity<Map<String, Object>> processRefund\(@PathVariable String refundNo\)/public ResponseEntity<Map<String, Object>> processRefund(\n            @Parameter(description = "退款单号", required = true) @PathVariable String refundNo)/;
' "$BACKEND_PATH/RefundController.java"

# PaymentController
echo "处理 PaymentController..."
perl -i -pe '
BEGIN { $done = 0; }
if (!$done && /^package com\.ccthub/) {
    $_ .= "\nimport io.swagger.v3.oas.annotations.Operation;\nimport io.swagger.v3.oas.annotations.Parameter;\nimport io.swagger.v3.oas.annotations.tags.Tag;\n";
}
if (!$done && /@RestController/) {
    $_ = "\@Tag(name = \"支付管理\", description = \"支付相关接口，包括创建支付订单、支付回调处理和支付查询等功能\")\n" . $_;
    $done = 1;
}
' "$BACKEND_PATH/PaymentController.java"

# CouponController
echo "处理 CouponController..."
perl -i -pe '
BEGIN { $done = 0; }
if (!$done && /^package com\.ccthub/) {
    $_ .= "\nimport io.swagger.v3.oas.annotations.Operation;\nimport io.swagger.v3.oas.annotations.Parameter;\nimport io.swagger.v3.oas.annotations.tags.Tag;\n";
}
if (!$done && /@RestController/) {
    $_ = "\@Tag(name = "优惠券管理", description = "优惠券相关接口，包括优惠券列表、创建、发放、使用和查询等功能")\n" . $_;
    $done = 1;
}
' "$BACKEND_PATH/CouponController.java"

# ActivityController
echo "处理 ActivityController..."
perl -i -pe '
BEGIN { $done = 0; }
if (!$done && /^package com\.ccthub/) {
    $_ .= "\nimport io.swagger.v3.oas.annotations.Operation;\nimport io.swagger.v3.oas.annotations.Parameter;\nimport io.swagger.v3.oas.annotations.tags.Tag;\n";
}
if (!$done && /@RestController/) {
    $_ = "\@Tag(name = \"活动管理\", description = \"活动相关接口，包括活动列表、创建、审核、上下线和删除等功能\")\n" . $_;
    $done = 1;
}
' "$BACKEND_PATH/ActivityController.java"

# ParticipationController
echo "处理 ParticipationController..."
perl -i -pe '
BEGIN { $done = 0; }
if (!$done && /^package com\.ccthub/) {
    $_ .= "\nimport io.swagger.v3.oas.annotations.Operation;\nimport io.swagger.v3.oas.annotations.Parameter;\nimport io.swagger.v3.oas.annotations.tags.Tag;\n";
}
if (!$done && /@RestController/) {
    $_ = "\@Tag(name = \"活动参与管理\", description = \"用户参与活动相关接口，包括参与活动、打卡、完成、放弃和奖励发放等功能\")\n" . $_;
    $done = 1;
}
' "$BACKEND_PATH/ParticipationController.java"

# VerificationController
echo "处理 VerificationController..."
perl -i -pe '
BEGIN { $done = 0; }
if (!$done && /^package com\.ccthub/) {
    $_ .= "\nimport io.swagger.v3.oas.annotations.Operation;\nimport io.swagger.v3.oas.annotations.Parameter;\nimport io.swagger.v3.oas.annotations.tags.Tag;\n";
}
if (!$done && /@RestController/) {
    $_ = "\@Tag(name = \"核销管理\", description = \"电子票券核销相关接口，包括核销码查询、单个核销、批量核销和核销记录查询等功能\")\n" . $_;
    $done = 1;
}
' "$BACKEND_PATH/VerificationController.java"

# TicketOrderController
echo "处理 TicketOrderController..."
perl -i -pe '
BEGIN { $done = 0; }
if (!$done && /^package com\.ccthub/) {
    $_ .= "\nimport io.swagger.v3.oas.annotations.Operation;\nimport io.swagger.v3.oas.annotations.Parameter;\nimport io.swagger.v3.oas.annotations.tags.Tag;\n";
}
if (!$done && /@RestController/) {
    $_ = "\@Tag(name = \"门票订单管理\", description = \"门票订单相关接口，包括订单创建、查询、支付和取消等功能\")\n" . $_;
    $done = 1;
}
' "$BACKEND_PATH/TicketOrderController.java"

# UnifiedOrderController
echo "处理 UnifiedOrderController..."
perl -i -pe '
BEGIN { $done = 0; }
if (!$done && /^package com\.ccthub/) {
    $_ .= "\nimport io.swagger.v3.oas.annotations.Operation;\nimport io.swagger.v3.oas.annotations.Parameter;\nimport io.swagger.v3.oas.annotations.tags.Tag;\n";
}
if (!$done && /@RestController/) {
    $_ = "\@Tag(name = \"统一订单查询\", description = \"支持多种订单类型（门票/商品/活动）的统一查询接口\")\n" . $_;
    $done = 1;
}
' "$BACKEND_PATH/UnifiedOrderController.java"

echo "完成！"
