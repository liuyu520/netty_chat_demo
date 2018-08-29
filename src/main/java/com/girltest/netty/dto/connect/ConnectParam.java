package com.girltest.netty.dto.connect;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConnectParam {
    private int port;
    private String socketIp = "localhost";

}
