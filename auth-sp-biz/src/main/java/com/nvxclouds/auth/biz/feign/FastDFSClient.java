package com.nvxclouds.auth.biz.feign;

import com.nvxclouds.fastdfs.api.feign.FileFeign;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @Auther: zhengxing.hu
 * @Date: 2020/6/2 18:31
 * @Description:
 */
@FeignClient(value = "fastdfs")
public interface FastDFSClient extends FileFeign {
}
