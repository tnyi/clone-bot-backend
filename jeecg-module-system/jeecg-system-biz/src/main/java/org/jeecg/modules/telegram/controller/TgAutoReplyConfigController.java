package org.jeecg.modules.telegram.controller;

import java.util.Arrays;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.modules.telegram.entity.TgAutoReplyConfig;
import org.jeecg.modules.telegram.service.ITgAutoReplyConfigService;

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
 * @Description: 自动回复配置
 * @Author: jeecg-boot
 * @Date:   2023-11-15
 * @Version: V1.0
 */
@Api(tags="自动回复配置")
@RestController
@RequestMapping("/bot/tgAutoReplyConfig")
@Slf4j
public class TgAutoReplyConfigController extends JeecgController<TgAutoReplyConfig, ITgAutoReplyConfigService> {
	@Autowired
	private ITgAutoReplyConfigService tgAutoReplyConfigService;

	/**
	 * 分页列表查询
	 *
	 * @param tgAutoReplyConfig
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "自动回复配置-分页列表查询")
	@ApiOperation(value="自动回复配置-分页列表查询", notes="自动回复配置-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<TgAutoReplyConfig>> queryPageList(TgAutoReplyConfig tgAutoReplyConfig,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<TgAutoReplyConfig> queryWrapper = QueryGenerator.initQueryWrapper(tgAutoReplyConfig, req.getParameterMap());
		Page<TgAutoReplyConfig> page = new Page<TgAutoReplyConfig>(pageNo, pageSize);
		IPage<TgAutoReplyConfig> pageList = tgAutoReplyConfigService.page(page, queryWrapper);
		return Result.OK(pageList);
	}

	/**
	 *   添加
	 *
	 * @param tgAutoReplyConfig
	 * @return
	 */
	@AutoLog(value = "自动回复配置-添加")
	@ApiOperation(value="自动回复配置-添加", notes="自动回复配置-添加")
	@RequiresPermissions("bot:tg_auto_reply_config:add")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody TgAutoReplyConfig tgAutoReplyConfig) {
		tgAutoReplyConfigService.save(tgAutoReplyConfig);
		return Result.OK("添加成功！");
	}

	/**
	 *  编辑
	 *
	 * @param tgAutoReplyConfig
	 * @return
	 */
	@AutoLog(value = "自动回复配置-编辑")
	@ApiOperation(value="自动回复配置-编辑", notes="自动回复配置-编辑")
	@RequiresPermissions("bot:tg_auto_reply_config:edit")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody TgAutoReplyConfig tgAutoReplyConfig) {
		tgAutoReplyConfigService.updateById(tgAutoReplyConfig);
		return Result.OK("编辑成功!");
	}

	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "自动回复配置-通过id删除")
	@ApiOperation(value="自动回复配置-通过id删除", notes="自动回复配置-通过id删除")
	@RequiresPermissions("bot:tg_auto_reply_config:delete")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		tgAutoReplyConfigService.removeById(id);
		return Result.OK("删除成功!");
	}

	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "自动回复配置-批量删除")
	@ApiOperation(value="自动回复配置-批量删除", notes="自动回复配置-批量删除")
	@RequiresPermissions("bot:tg_auto_reply_config:deleteBatch")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.tgAutoReplyConfigService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "自动回复配置-通过id查询")
	@ApiOperation(value="自动回复配置-通过id查询", notes="自动回复配置-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<TgAutoReplyConfig> queryById(@RequestParam(name="id",required=true) String id) {
		TgAutoReplyConfig tgAutoReplyConfig = tgAutoReplyConfigService.getById(id);
		if(tgAutoReplyConfig==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(tgAutoReplyConfig);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param tgAutoReplyConfig
    */
    @RequiresPermissions("bot:tg_auto_reply_config:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, TgAutoReplyConfig tgAutoReplyConfig) {
        return super.exportXls(request, tgAutoReplyConfig, TgAutoReplyConfig.class, "自动回复配置");
    }

    /**
      * 通过excel导入数据
    *
    * @param request
    * @param response
    * @return
    */
    @RequiresPermissions("bot:tg_auto_reply_config:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, TgAutoReplyConfig.class);
    }

}
