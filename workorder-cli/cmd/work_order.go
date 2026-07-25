package cmd

import (
	"context"
	"fmt"

	"workorder-cli/internal/api"
	"workorder-cli/internal/output"
)

func handleWorkOrderCommand(args []string) {
	if len(args) < 2 {
		output.PrintError(4, "缺少子命令，可用子命令: page, detail, search")
		return
	}

	subCmd := args[1]

	switch subCmd {
	case "page":
		handleWorkOrderPage(args)
	case "detail":
		handleWorkOrderDetail(args)
	case "search":
		handleWorkOrderSearch(args)
	default:
		output.PrintError(4, fmt.Sprintf("未知子命令: %s，可用子命令: page, detail, search", subCmd))
	}
}

func handleWorkOrderPage(args []string) {
	pageNum := parseIntArg(args, "--page-num", 1)
	pageSize := parseIntArg(args, "--page-size", 10)
	title := getArgValue(args, "--title")
	code := getArgValue(args, "--code")
	workType := parseIntArg(args, "--type", -1)
	content := getArgValue(args, "--content")
	createTimeTo := parseIntArg(args, "--create-time-to", 0)

	client := api.NewClient()
	ctx := context.Background()

	params := map[string]interface{}{
		"pageNum":  pageNum,
		"pageSize": pageSize,
	}
	if title != "" {
		params["title"] = title
	}
	if code != "" {
		params["code"] = code
	}
	if workType != -1 {
		params["type"] = workType
	}
	if content != "" {
		params["content"] = content
	}
	if createTimeTo != 0 {
		params["createTimeTo"] = createTimeTo
	}

	headers := api.GetAuthHeaders()

	resp, err := client.Query(ctx, "work_order_page", params, headers)
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

func handleWorkOrderDetail(args []string) {
	id := parseIntArg(args, "--id", 0)
	code := getArgValue(args, "--code")

	client := api.NewClient()
	ctx := context.Background()

	params := map[string]interface{}{}
	if id != 0 {
		params["id"] = id
	} else if code != "" {
		params["code"] = code
	} else {
		output.PrintError(4, "必须指定id或code参数")
		return
	}

	headers := api.GetAuthHeaders()

	resp, err := client.Query(ctx, "work_order_detail", params, headers)
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

func handleWorkOrderSearch(args []string) {
	keyword := getArgValue(args, "--keyword")
	pageNum := parseIntArg(args, "--page-num", 1)
	pageSize := parseIntArg(args, "--page-size", 10)

	if keyword == "" {
		output.PrintError(4, "keyword参数不能为空")
		return
	}

	client := api.NewClient()
	ctx := context.Background()

	params := map[string]interface{}{
		"keyword":  keyword,
		"pageNum":  pageNum,
		"pageSize": pageSize,
	}

	headers := api.GetAuthHeaders()

	resp, err := client.Query(ctx, "work_order_search", params, headers)
	if err != nil {
		output.PrintError(1, fmt.Sprintf("搜索失败: %v", err))
		return
	}

	output.PrintResponse(output.Response{
		Code:    resp.Code,
		Message: resp.Message,
		Data:    resp.Data,
		TraceId: resp.TraceId,
	})
}
