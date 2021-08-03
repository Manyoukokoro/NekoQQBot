package org.nekotori.service.impl;

import org.nekotori.service.GroupService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author: JayDeng
 * @date: 03/08/2021 10:04
 * @description:
 * @version: {@link }
 */
@Service
public class GroupServiceImpl implements GroupService {

    @Value("${bot.privilege-groups}")
    private Long[] ids;

    @Override
    public boolean checkPrivilege(Long groupId) {
        if(List.of(ids).contains(groupId)) return true;
        return false;
    }
}
    