package com.quark.porent.controller;

import com.quark.common.base.BaseController;
import com.quark.common.dto.QuarkResult;
import com.quark.common.entity.Reply;
import com.quark.common.entity.User;
import com.quark.porent.interceptor.SubjectHolder;
import com.quark.porent.service.ReplyService;
import com.quark.porent.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author LHR
 * Create By 2017/8/30
 */
@Api(value = "回复接口", description = "对帖子进行回复,点赞回复等服务")
@RestController
@RequestMapping("/reply")
public class ReplyController extends BaseController{

    @Autowired
    private WebSocketController webSocketController;

    @Autowired
    private UserService userService;

    @Autowired
    private ReplyService replyService;
    @ApiOperation("发布回复接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "content", value = "回复内容", dataType = "String"),
            @ApiImplicitParam(name = "token", value = "用户令牌", dataType = "String"),
            @ApiImplicitParam(name = "postsId", value = "帖子ID", dataType = "Integer")
    })
    @PostMapping("/api")
    public QuarkResult CreateReply(Reply reply,Integer postsId){
        QuarkResult result = restProcessor(() -> {
            User user = SubjectHolder.get();
            if (user.getEnable() != 1) return QuarkResult.warn("用户处于封禁状态！");

            replyService.saveReply(reply, postsId, user);
            return QuarkResult.ok();
        });
        return result;
    }


}
