package com.kongtrolink.yyjw.service;

import com.kongtrolink.yyjw.vo.DirectoryVo;

import java.util.List;

/**
 * \* @Author: mystox
 * \* Date: 2018/10/12 11:30
 * \* Description:
 * \
 */
public interface ResourceService {

    List<String> compareResources(List<String> fileNames);

    String getAppVersion(String appId, String os);

    String getDownLoadUrl(String appId);

    List<DirectoryVo> getDocumentList();
}