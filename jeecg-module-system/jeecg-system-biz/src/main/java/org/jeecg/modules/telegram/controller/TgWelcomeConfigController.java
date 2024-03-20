package org.jeecg.modules.telegram.controller;

import java.util.Arrays;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.modules.telegram.entity.TgWelcomeConfig;
import org.jeecg.modules.telegram.service.ITgWelcomeConfigService;

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
 * @Description: 欢迎语配置
 * @Author: jeecg-boot
 * @Date:   2023-11-15
 * @Version: V1.0
 */
@Api(tags="欢迎语配置")
@RestController
@RequestMapping("/bot/tgWelcomeConfig")
@Slf4j
public class TgWelcomeConfigController extends JeecgController<TgWelcomeConfig, ITgWelcomeConfigService> {
	@Autowired
	private ITgWelcomeConfigService tgWelcomeConfigService;

	/**
	 * 分页列表查询
	 *
	 * @param tgWelcomeConfig
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "欢迎语配置-分页列表查询")
	@ApiOperation(value="欢迎语配置-分页列表查询", notes="欢迎语配置-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<TgWelcomeConfig>> queryPageList(TgWelcomeConfig tgWelcomeConfig,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<TgWelcomeConfig> queryWrapper = QueryGenerator.initQueryWrapper(tgWelcomeConfig, req.getParameterMap());
		Page<TgWelcomeConfig> page = new Page<TgWelcomeConfig>(pageNo, pageSize);
		IPage<TgWelcomeConfig> pageList = tgWelcomeConfigService.page(page, queryWrapper);
		return Result.OK(pageList);
	}

	/**
	 *   添加
	 *
	 * @param tgWelcomeConfig
	 * @return
	 */
	@AutoLog(value = "欢迎语配置-添加")
	@ApiOperation(value="欢迎语配置-添加", notes="欢迎语配置-添加")
	@RequiresPermissions("bot:tg_welcome_config:add")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody TgWelcomeConfig tgWelcomeConfig) {
		tgWelcomeConfigService.save(tgWelcomeConfig);
		return Result.OK("添加成功！");
	}

	/**
	 *  编辑
	 *
	 * @param tgWelcomeConfig
	 * @return
	 */
	@AutoLog(value = "欢迎语配置-编辑")
	@ApiOperation(value="欢迎语配置-编辑", notes="欢迎语配置-编辑")
	@RequiresPermissions("bot:tg_welcome_config:edit")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody TgWelcomeConfig tgWelcomeConfig) {
		tgWelcomeConfigService.updateById(tgWelcomeConfig);
		return Result.OK("编辑成功!");
	}

	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "欢迎语配置-通过id删除")
	@ApiOperation(value="欢迎语配置-通过id删除", notes="欢迎语配置-通过id删除")
	@RequiresPermissions("bot:tg_welcome_config:delete")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		tgWelcomeConfigService.removeById(id);
		return Result.OK("删除成功!");
	}

	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "欢迎语配置-批量删除")
	@ApiOperation(value="欢迎语配置-批量删除", notes="欢迎语配置-批量删除")
	@RequiresPermissions("bot:tg_welcome_config:deleteBatch")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.tgWelcomeConfigService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "欢迎语配置-通过id查询")
	@ApiOperation(value="欢迎语配置-通过id查询", notes="欢迎语配置-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<TgWelcomeConfig> queryById(@RequestParam(name="id",required=true) String id) {
		TgWelcomeConfig tgWelcomeConfig = tgWelcomeConfigService.getById(id);
		if(tgWelcomeConfig==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(tgWelcomeConfig);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param tgWelcomeConfig
    */
    @RequiresPermissions("bot:tg_welcome_config:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, TgWelcomeConfig tgWelcomeConfig) {
        return super.exportXls(request, tgWelcomeConfig, TgWelcomeConfig.class, "欢迎语配置");
    }

    /**
      * 通过excel导入数据
    *
    * @param request
    * @param response
    * @return
    */
    @RequiresPermissions("bot:tg_welcome_config:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, TgWelcomeConfig.class);
    }

}
