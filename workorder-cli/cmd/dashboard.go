package cmd

import (
	"context"
	"fmt"

	"workorder-cli/internal/api"
	"workorder-cli/internal/output"
)

func handleDashboardCommand(args []string) {
	if len(args) < 2 {
		output.PrintError(4, "缺少子命令，可用子命令: data, handle_quantity, messages")
		return
	}

	subCmd := args[1]

	switch subCmd {
	case "data":
		handleDashboardData(args)
	case "handle_quantity":
		handleDashboardHandleQuantity(args)
	case "messages":
		handleDashboardMessages(args)
	default:
		output.PrintError(4, fmt.Sprintf("未知子命令: %s，可用子命令: data, handle_quantity, messages", subCmd))
	}
}

func handleDashboardData(args []string) {
	client := api.NewClient()
	ctx := context.Background()

	headers := api.GetAuthHeaders()

	resp, err := client.Query(ctx, "dashboard_data", nil, headers)
	if err != nil {
		output.PrintError(1, fmt.Sprintf("获取失败: %v", err))
		return
	}

	output.PrintResponse(output.Response{
		Code:    resp.Code,
		Message: resp.Message,
		Data:    resp.Data,
		TraceId: resp.TraceId,
	})
}

func handleDashboardHandleQuantity(args []string) {
	client := api.NewClient()
	ctx := context.Background()

	headers := api.GetAuthHeaders()

	resp, err := client.Query(ctx, "dashboard_handle_quantity", nil, headers)
	if err != nil {
		output.PrintError(1, fmt.Sprintf("获取失败: %v", err))
		return
	}

	output.PrintResponse(output.Response{
		Code:    resp.Code,
		Message: resp.Message,
		Data:    resp.Data,
		TraceId: resp.TraceId,
	})
}

func handleDashboardMessages(args []string) {
	pageNum := parseIntArg(args, "--page-num", 1)
	pageSize := parseIntArg(args, "--page-size", 10)

	client := api.NewClient()
	ctx := context.Background()

	params := map[string]interface{}{
		"pageNum":  pageNum,
		"pageSize": pageSize,
	}

	headers := api.GetAuthHeaders()

	resp, err := client.Query(ctx, "dashboard_messages", params, headers)
	if err != nil {
		output.PrintError(1, fmt.Sprintf("获取失败: %v", err))
		return
	}

	output.PrintResponse(output.Response{
		Code:    resp.Code,
		Message: resp.Message,
		Data:    resp.Data,
		TraceId: resp.TraceId,
	})
}
