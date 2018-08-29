package com.girltest.netty.listener;

import com.common.thread.ThreadPoolUtil;
import com.girltest.netty.handle.console.server.IChannelListener;
import com.girltest.netty.util.PrintUtil;
import com.string.widget.util.ValueWidget;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WaitingForUserInputListener {
    private IChannelListener channelListener;

    /**
     * 等待用户输入
     */
    public void execute() {
        ThreadPoolUtil.execute(() -> {
            BufferedReader reader =
                    new BufferedReader(new InputStreamReader(System.in));
            try {
                while (true) {
                    // Reading data using readLine
                    String input = null;
                    try {
                        input = reader.readLine();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    PrintUtil.print("input:" + input);
                    if (!ValueWidget.isNullOrEmpty(input)) {
                        channelListener.consoleInputHandle(input);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (null != reader) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }

        });
    }


}
