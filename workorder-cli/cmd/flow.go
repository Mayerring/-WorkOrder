package cmd

import (
	"context"
	"fmt"

	"workorder-cli/internal/api"
	"workorder-cli/internal/output"
)

func handleFlowCommand(args []string) {
	if len(args) < 2 {
		output.PrintError(4, "缺少子命令，可用子命令: getById, page")
		return
	}

	subCmd := args[1]

	switch subCmd {
	case "getById":
		handleFlowGetById(args)
	case "page":
		handleFlowPage(args)
	default:
		output.PrintError(4, fmt.Sprintf("未知子命令: %s，可用子命令: getById, page", subCmd))
	}
}

func handleFlowGetById(args []string) {
	flowId := parseIntArg(args, "--id", 0)

	if flowId == 0 {
		output.PrintError(4, "id参数不能为空")
		return
	}

	client := api.NewClient()
	ctx := context.Background()

	params := map[string]interface{}{
		"flowId": flowId,
	}

	headers := api.GetAuthHeaders()

	resp, err := client.Query(ctx, "flow_get_by_id", params, headers)
	if err != nil {
		output.PrintError(1, fmt.Sprintf("查询失败: %v", err))
		return
	}

	output.PrintResponse(output.Response{
		Code:    resp.Code,
		Message: resp.Message,
		Data:    resp.Data,
		TraceId: resp.TraceId,
	})
}

func handleFlowPage(args []string) {
	pageNum := parseIntArg(args, "--page-num", 1)
	pageSize := parseIntArg(args, "--page-size", 10)

	client := api.NewClient()
	ctx := context.Background()

	params := map[string]interface{}{
		"pageNum":  pageNum,
		"pageSize": pageSize,
	}

	headers := api.GetAuthHeaders()

	resp, err := client.Query(ctx, "flow_page", params, headers)
	if err != nil {
		output.PrintError(1, fmt.Sprintf("查询失败: %v", err))
		return
	}

	output.PrintResponse(output.Response{
		Code:    resp.Code,
		Message: resp.Message,
		Data:    resp.Data,
		TraceId: resp.TraceId,
	})
}
