package cmd

import (
	"context"
	"fmt"

	"workorder-cli/internal/api"
	"workorder-cli/internal/output"

	"github.com/spf13/viper"
)

func handleSchemaCommand(args []string) {
	if len(args) < 2 {
		output.PrintError(4, "dataCode不能为空")
		return
	}
	dataCode := args[1]

	client := api.NewClient()
	ctx := context.Background()

	url := fmt.Sprintf("%s/api/schema/%s", viper.GetString("cli-service-url"), dataCode)
	headers := api.GetAuthHeaders()

	resp, err := client.Get(ctx, url, headers)
	if err != nil {
		output.PrintError(1, fmt.Sprintf("获取Schema失败: %v", err))
		return
	}

	output.PrintResponse(output.Response{
		Code:    resp.Code,
		Message: resp.Message,
		Data:    resp.Data,
		TraceId: resp.TraceId,
	})
}
