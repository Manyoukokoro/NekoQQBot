package org.nekotori.service.impl;

import org.nekotori.service.GroupService;
import org.springframework.stereotype.Service;

/**
 * @author: JayDeng
 * @date: 03/08/2021 10:04
 * @description:
 * @version: {@link }
 */
@Service
public class GroupServiceImpl implements GroupService {
    @Override
    public boolean checkPrivilege(Long groupId) {
        if(groupId == 1026836775L) return true;
        return false;
    }
}
    