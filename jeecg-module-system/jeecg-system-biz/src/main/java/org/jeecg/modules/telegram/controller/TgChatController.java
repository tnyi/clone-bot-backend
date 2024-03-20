package org.jeecg.modules.telegram.controller;

import java.util.Arrays;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.modules.telegram.entity.TgChat;
import org.jeecg.modules.telegram.service.ITgChatService;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

import org.jeecg.common.system.base.controller.JeecgController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.apache.shiro.authz.annotation.RequiresPermissions;

 /**
 * @Description: 聊天信息表
 * @Author: jeecg-boot
 * @Date:   2023-11-15
 * @Version: V1.0
 */
@Api(tags="聊天信息表")
@RestController
@RequestMapping("/bot/tgChat")
@Slf4j
public class TgChatController extends JeecgController<TgChat, ITgChatService> {
	@Autowired
	private ITgChatService tgChatService;

	/**
	 * 分页列表查询
	 *
	 * @param tgChat
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "聊天信息表-分页列表查询")
	@ApiOperation(value="聊天信息表-分页列表查询", notes="聊天信息表-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<TgChat>> queryPageList(TgChat tgChat,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<TgChat> queryWrapper = QueryGenerator.initQueryWrapper(tgChat, req.getParameterMap());
		Page<TgChat> page = new Page<TgChat>(pageNo, pageSize);
		IPage<TgChat> pageList = tgChatService.page(page, queryWrapper);
		return Result.OK(pageList);
	}

	/**
	 *   添加
	 *
	 * @param tgChat
	 * @return
	 */
	@AutoLog(value = "聊天信息表-添加")
	@ApiOperation(value="聊天信息表-添加", notes="聊天信息表-添加")
	@RequiresPermissions("bot:tg_chat:add")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody TgChat tgChat) {
		tgChatService.save(tgChat);
		return Result.OK("添加成功！");
	}

	/**
	 *  编辑
	 *
	 * @param tgChat
	 * @return
	 */
	@AutoLog(value = "聊天信息表-编辑")
	@ApiOperation(value="聊天信息表-编辑", notes="聊天信息表-编辑")
	@RequiresPermissions("bot:tg_chat:edit")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody TgChat tgChat) {
		tgChatService.updateById(tgChat);
		return Result.OK("编辑成功!");
	}

	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "聊天信息表-通过id删除")
	@ApiOperation(value="聊天信息表-通过id删除", notes="聊天信息表-通过id删除")
	@RequiresPermissions("bot:tg_chat:delete")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		tgChatService.removeById(id);
		return Result.OK("删除成功!");
	}

	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "聊天信息表-批量删除")
	@ApiOperation(value="聊天信息表-批量删除", notes="聊天信息表-批量删除")
	@RequiresPermissions("bot:tg_chat:deleteBatch")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.tgChatService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "聊天信息表-通过id查询")
	@ApiOperation(value="聊天信息表-通过id查询", notes="聊天信息表-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<TgChat> queryById(@RequestParam(name="id",required=true) String id) {
		TgChat tgChat = tgChatService.getById(id);
		if(tgChat==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(tgChat);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param tgChat
    */
    @RequiresPermissions("bot:tg_chat:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, TgChat tgChat) {
        return super.exportXls(request, tgChat, TgChat.class, "聊天信息表");
    }

    /**
      * 通过excel导入数据
    *
    * @param request
    * @param response
    * @return
    */
    @RequiresPermissions("bot:tg_chat:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, TgChat.class);
    }

}
