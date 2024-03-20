package org.jeecg.modules.telegram.controller;

import java.util.Arrays;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.modules.telegram.entity.TgMsgTemplate;
import org.jeecg.modules.telegram.service.ITgMsgTemplateService;

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
 * @Description: 消息模板表
 * @Author: jeecg-boot
 * @Date:   2023-11-15
 * @Version: V1.0
 */
@Api(tags="消息模板表")
@RestController
@RequestMapping("/bot/tgMsgTemplate")
@Slf4j
public class TgMsgTemplateController extends JeecgController<TgMsgTemplate, ITgMsgTemplateService> {
	@Autowired
	private ITgMsgTemplateService tgMsgTemplateService;

	/**
	 * 分页列表查询
	 *
	 * @param tgMsgTemplate
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "消息模板表-分页列表查询")
	@ApiOperation(value="消息模板表-分页列表查询", notes="消息模板表-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<TgMsgTemplate>> queryPageList(TgMsgTemplate tgMsgTemplate,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<TgMsgTemplate> queryWrapper = QueryGenerator.initQueryWrapper(tgMsgTemplate, req.getParameterMap());
		Page<TgMsgTemplate> page = new Page<TgMsgTemplate>(pageNo, pageSize);
		IPage<TgMsgTemplate> pageList = tgMsgTemplateService.page(page, queryWrapper);
		return Result.OK(pageList);
	}

	/**
	 *   添加
	 *
	 * @param tgMsgTemplate
	 * @return
	 */
	@AutoLog(value = "消息模板表-添加")
	@ApiOperation(value="消息模板表-添加", notes="消息模板表-添加")
	@RequiresPermissions("bot:tg_msg_template:add")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody TgMsgTemplate tgMsgTemplate) {
		tgMsgTemplateService.save(tgMsgTemplate);
		return Result.OK("添加成功！");
	}

	/**
	 *  编辑
	 *
	 * @param tgMsgTemplate
	 * @return
	 */
	@AutoLog(value = "消息模板表-编辑")
	@ApiOperation(value="消息模板表-编辑", notes="消息模板表-编辑")
	@RequiresPermissions("bot:tg_msg_template:edit")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody TgMsgTemplate tgMsgTemplate) {
		tgMsgTemplateService.updateById(tgMsgTemplate);
		return Result.OK("编辑成功!");
	}

	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "消息模板表-通过id删除")
	@ApiOperation(value="消息模板表-通过id删除", notes="消息模板表-通过id删除")
	@RequiresPermissions("bot:tg_msg_template:delete")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		tgMsgTemplateService.removeById(id);
		return Result.OK("删除成功!");
	}

	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "消息模板表-批量删除")
	@ApiOperation(value="消息模板表-批量删除", notes="消息模板表-批量删除")
	@RequiresPermissions("bot:tg_msg_template:deleteBatch")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.tgMsgTemplateService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "消息模板表-通过id查询")
	@ApiOperation(value="消息模板表-通过id查询", notes="消息模板表-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<TgMsgTemplate> queryById(@RequestParam(name="id",required=true) String id) {
		TgMsgTemplate tgMsgTemplate = tgMsgTemplateService.getById(id);
		if(tgMsgTemplate==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(tgMsgTemplate);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param tgMsgTemplate
    */
    @RequiresPermissions("bot:tg_msg_template:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, TgMsgTemplate tgMsgTemplate) {
        return super.exportXls(request, tgMsgTemplate, TgMsgTemplate.class, "消息模板表");
    }

    /**
      * 通过excel导入数据
    *
    * @param request
    * @param response
    * @return
    */
    @RequiresPermissions("bot:tg_msg_template:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, TgMsgTemplate.class);
    }

}
