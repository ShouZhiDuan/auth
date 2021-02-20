package com.nvxclouds.auth.biz;

import com.nvxclouds.auth.biz.feign.NotifyClient;
import com.nvxclouds.common.base.pojo.BaseResult;
import com.nvxclouds.notify.api.dto.LoginMessageDTO;
import com.nvxclouds.notify.api.dto.RegisterMessageDTO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @Auther: zhengxing.hu
 * @Date: 2020/6/2 10:24
 * @Description:
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class FeignTest {

    @Autowired
    private NotifyClient notifyFeign;


    @Test
    public void testSendLoginMessage() {
        LoginMessageDTO dto = new LoginMessageDTO();
        dto.setPhone("13995600740");
        dto.setMessage("test");
        notifyFeign.sendLoginMessage(dto);
    }

    @Test
    public void testSendRegisterMessage() {
        RegisterMessageDTO dto = new RegisterMessageDTO();
        dto.setPhone("13758262702");
        dto.setMessage("testRegister");
        notifyFeign.sendRegisterMessage(dto);
    }
}
