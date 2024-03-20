package org.jeecg.modules.telegram.controller;

import java.util.Arrays;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.modules.telegram.entity.TgBlacklist;
import org.jeecg.modules.telegram.service.ITgBlacklistService;

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
 * @Description: 黑名单表
 * @Author: jeecg-boot
 * @Date:   2023-11-15
 * @Version: V1.0
 */
@Api(tags="黑名单表")
@RestController
@RequestMapping("/bot/tgBlacklist")
@Slf4j
public class TgBlacklistController extends JeecgController<TgBlacklist, ITgBlacklistService> {
	@Autowired
	private ITgBlacklistService tgBlacklistService;

	/**
	 * 分页列表查询
	 *
	 * @param tgBlacklist
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "黑名单表-分页列表查询")
	@ApiOperation(value="黑名单表-分页列表查询", notes="黑名单表-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<TgBlacklist>> queryPageList(TgBlacklist tgBlacklist,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<TgBlacklist> queryWrapper = QueryGenerator.initQueryWrapper(tgBlacklist, req.getParameterMap());
		Page<TgBlacklist> page = new Page<TgBlacklist>(pageNo, pageSize);
		IPage<TgBlacklist> pageList = tgBlacklistService.page(page, queryWrapper);
		return Result.OK(pageList);
	}

	/**
	 *   添加
	 *
	 * @param tgBlacklist
	 * @return
	 */
	@AutoLog(value = "黑名单表-添加")
	@ApiOperation(value="黑名单表-添加", notes="黑名单表-添加")
	@RequiresPermissions("bot:tg_blacklist:add")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody TgBlacklist tgBlacklist) {
		tgBlacklistService.save(tgBlacklist);
		return Result.OK("添加成功！");
	}

	/**
	 *  编辑
	 *
	 * @param tgBlacklist
	 * @return
	 */
	@AutoLog(value = "黑名单表-编辑")
	@ApiOperation(value="黑名单表-编辑", notes="黑名单表-编辑")
	@RequiresPermissions("bot:tg_blacklist:edit")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody TgBlacklist tgBlacklist) {
		tgBlacklistService.updateById(tgBlacklist);
		return Result.OK("编辑成功!");
	}

	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "黑名单表-通过id删除")
	@ApiOperation(value="黑名单表-通过id删除", notes="黑名单表-通过id删除")
	@RequiresPermissions("bot:tg_blacklist:delete")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		tgBlacklistService.removeById(id);
		return Result.OK("删除成功!");
	}

	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "黑名单表-批量删除")
	@ApiOperation(value="黑名单表-批量删除", notes="黑名单表-批量删除")
	@RequiresPermissions("bot:tg_blacklist:deleteBatch")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.tgBlacklistService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "黑名单表-通过id查询")
	@ApiOperation(value="黑名单表-通过id查询", notes="黑名单表-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<TgBlacklist> queryById(@RequestParam(name="id",required=true) String id) {
		TgBlacklist tgBlacklist = tgBlacklistService.getById(id);
		if(tgBlacklist==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(tgBlacklist);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param tgBlacklist
    */
    @RequiresPermissions("bot:tg_blacklist:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, TgBlacklist tgBlacklist) {
        return super.exportXls(request, tgBlacklist, TgBlacklist.class, "黑名单表");
    }

    /**
      * 通过excel导入数据
    *
    * @param request
    * @param response
    * @return
    */
    @RequiresPermissions("bot:tg_blacklist:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, TgBlacklist.class);
    }

}