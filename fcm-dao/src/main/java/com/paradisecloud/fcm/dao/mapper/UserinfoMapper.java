package com.paradisecloud.fcm.dao.mapper;

import com.paradisecloud.fcm.dao.model.Userinfo;

import java.util.List;

/**
 * 【FS注册用户】Mapper接口
 * 
 * @author lilinhai
 * @date 2024-07-15
 */
public interface UserinfoMapper 
{
    /**
     * 查询【FS注册用户】
     * 
     * @param id 【FS注册用户】ID
     * @return 【FS注册用户】
     */
    public Userinfo selectUserinfoById(Integer id);

    /**
     * 查询【FS注册用户】
     *
     * @param username 【FS注册用户】用户名
     * @return 【FS注册用户】
     */
    public Userinfo selectUserinfoByUsername(String username);

    /**
     * 批量删除【FS注册用户】
     *
     * @param usernames 需要删除的数据ID
     * @return 【FS注册用户】集合
     */
    public List<Userinfo> selectUserinfoByUsernames(String[] usernames);

    /**
     * 查询【FS注册用户】列表
     * 
     * @param userinfo 【FS注册用户】
     * @return 【FS注册用户】集合
     */
    public List<Userinfo> selectUserinfoList(Userinfo userinfo);

    /**
     * 新增【FS注册用户】
     * 
     * @param userinfo 【FS注册用户】
     * @return 结果
     */
    public int insertUserinfo(Userinfo userinfo);

    /**
     * 修改【FS注册用户】
     * 
     * @param userinfo 【FS注册用户】
     * @return 结果
     */
    public int updateUserinfo(Userinfo userinfo);

    /**
     * 删除【FS注册用户】
     * 
     * @param id 【FS注册用户】ID
     * @return 结果
     */
    public int deleteUserinfoById(Integer id);

    /**
     * 批量删除【FS注册用户】
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteUserinfoByIds(Integer[] ids);

    /**
     * 删除【FS注册用户】
     *
     * @param username 【FS注册用户】ID
     * @return 结果
     */
    public int deleteUserinfoByUsername(String username);

    /**
     * 批量删除【FS注册用户】
     *
     * @param usernames 需要删除的数据ID
     * @return 结果
     */
    public int deleteUserinfoByUsernames(String[] usernames);
}
