package cmd

import (
	"context"
	"fmt"

	"workorder-cli/internal/api"
	"workorder-cli/internal/output"

	"github.com/spf13/viper"
)

func handleListCommand(args []string) {
	client := api.NewClient()
	ctx := context.Background()

	url := fmt.Sprintf("%s/api/dataCodes", viper.GetString("cli-service-url"))
	headers := api.GetAuthHeaders()

	resp, err := client.Get(ctx, url, headers)
	if err != nil {
		output.PrintError(1, fmt.Sprintf("获取命令列表失败: %v", err))
		return
	}

	output.PrintResponse(output.Response{
		Code:    resp.Code,
		Message: resp.Message,
		Data:    resp.Data,
		TraceId: resp.TraceId,
	})
}
