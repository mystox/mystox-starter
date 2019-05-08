package com.kongtrolink.framework;

import com.kongtrolink.framework.runner.VpnRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MinifsuVpnApplicationTests {



	@MockBean
    VpnRunner towerRunner; //测试排除注入服务初始化
	@Test
	public void contextLoads() {
	}



}
