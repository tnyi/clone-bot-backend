package org.jeecg.modules.telegram.controller;

import java.util.Arrays;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.modules.telegram.entity.TgBot;
import org.jeecg.modules.telegram.service.ITgBotService;

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
 * @Description: 机器人表
 * @Author: jeecg-boot
 * @Date:   2023-11-15
 * @Version: V1.0
 */
@Api(tags="机器人表")
@RestController
@RequestMapping("/bot/tgBot")
@Slf4j
public class TgBotController extends JeecgController<TgBot, ITgBotService> {
	@Autowired
	private ITgBotService tgBotService;

	/**
	 * 分页列表查询
	 *
	 * @param tgBot
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "机器人表-分页列表查询")
	@ApiOperation(value="机器人表-分页列表查询", notes="机器人表-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<TgBot>> queryPageList(TgBot tgBot,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<TgBot> queryWrapper = QueryGenerator.initQueryWrapper(tgBot, req.getParameterMap());
		Page<TgBot> page = new Page<TgBot>(pageNo, pageSize);
		IPage<TgBot> pageList = tgBotService.page(page, queryWrapper);
		return Result.OK(pageList);
	}

	/**
	 *   添加
	 *
	 * @param tgBot
	 * @return
	 */
	@AutoLog(value = "机器人表-添加")
	@ApiOperation(value="机器人表-添加", notes="机器人表-添加")
	@RequiresPermissions("bot:tg_bot:add")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody TgBot tgBot) {
		tgBotService.save(tgBot);
		return Result.OK("添加成功！");
	}

	/**
	 *  编辑
	 *
	 * @param tgBot
	 * @return
	 */
	@AutoLog(value = "机器人表-编辑")
	@ApiOperation(value="机器人表-编辑", notes="机器人表-编辑")
	@RequiresPermissions("bot:tg_bot:edit")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody TgBot tgBot) {
		tgBotService.updateById(tgBot);
		return Result.OK("编辑成功!");
	}

	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "机器人表-通过id删除")
	@ApiOperation(value="机器人表-通过id删除", notes="机器人表-通过id删除")
	@RequiresPermissions("bot:tg_bot:delete")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		tgBotService.removeById(id);
		return Result.OK("删除成功!");
	}

	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "机器人表-批量删除")
	@ApiOperation(value="机器人表-批量删除", notes="机器人表-批量删除")
	@RequiresPermissions("bot:tg_bot:deleteBatch")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.tgBotService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "机器人表-通过id查询")
	@ApiOperation(value="机器人表-通过id查询", notes="机器人表-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<TgBot> queryById(@RequestParam(name="id",required=true) String id) {
		TgBot tgBot = tgBotService.getById(id);
		if(tgBot==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(tgBot);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param tgBot
    */
    @RequiresPermissions("bot:tg_bot:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, TgBot tgBot) {
        return super.exportXls(request, tgBot, TgBot.class, "机器人表");
    }

    /**
      * 通过excel导入数据
    *
    * @param request
    * @param response
    * @return
    */
    @RequiresPermissions("bot:tg_bot:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, TgBot.class);
    }

}
