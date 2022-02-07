package com.leqiwl.novel.admin.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.leqiwl.novel.common.base.ApiResult;
import com.leqiwl.novel.domain.dto.CrawlerRuleEditInDto;
import com.leqiwl.novel.domain.dto.CrawlerRuleEditOutDto;
import com.leqiwl.novel.domain.dto.XpathTestInDto;
import com.leqiwl.novel.domain.dto.XpathTestOutDto;
import com.leqiwl.novel.domain.entify.crawler.CrawlerRule;
import com.leqiwl.novel.service.CrawlerRuleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author: 飞鸟不过江
 * @Date: 2022/1/29 15:51
 * @Description:
 */
@Slf4j
@Controller
@RequestMapping("/rule")
public class SpiderRuleController {

    @Resource
    private CrawlerRuleService crawlerRuleService;


    @GetMapping("/view")
    public String getRuleView(){
        return "view/spider/rule";
    }

    @GetMapping("/testView")
    public String getTestView(){
        return "view/spider/ruleTest";
    }


    @GetMapping({"/edit","/edit/{ruleId}"})
    public String getRuleEditView(@PathVariable(required = false) String ruleId, Model model){
        if(StrUtil.isNotBlank(ruleId)){
            CrawlerRule crawlerRule = crawlerRuleService.getByRuleId(ruleId);
            CrawlerRuleEditOutDto crawlerRuleEditOutDto = new CrawlerRuleEditOutDto();
            BeanUtil.copyProperties(crawlerRule,crawlerRuleEditOutDto);
            BeanUtil.copyProperties(crawlerRule.getContentRule(),crawlerRuleEditOutDto);
            BeanUtil.copyProperties(crawlerRule.getDetailRule(),crawlerRuleEditOutDto);
            BeanUtil.copyProperties(crawlerRule.getListRule(),crawlerRuleEditOutDto);
            crawlerRuleEditOutDto.setContentNameRule(crawlerRule.getContentRule().getNameRule());
            model.addAttribute("ruleData",crawlerRuleEditOutDto);
        }
        return "view/spider/ruleEdit";
    }


    @GetMapping("/copy/{ruleId}")
    public String getRuleCopyView(@PathVariable(required = false) String ruleId, Model model){
        if(StrUtil.isNotBlank(ruleId)){
            CrawlerRule crawlerRule = crawlerRuleService.getByRuleId(ruleId);
            CrawlerRuleEditOutDto crawlerRuleEditOutDto = new CrawlerRuleEditOutDto();
            BeanUtil.copyProperties(crawlerRule,crawlerRuleEditOutDto);
            BeanUtil.copyProperties(crawlerRule.getContentRule(),crawlerRuleEditOutDto);
            BeanUtil.copyProperties(crawlerRule.getDetailRule(),crawlerRuleEditOutDto);
            BeanUtil.copyProperties(crawlerRule.getListRule(),crawlerRuleEditOutDto);
            crawlerRuleEditOutDto.setContentNameRule(crawlerRule.getContentRule().getNameRule());
            crawlerRuleEditOutDto.setRuleId(null);
            model.addAttribute("ruleData",crawlerRuleEditOutDto);
        }
        return "view/spider/ruleEdit";
    }

    @PostMapping("/add")
    @ResponseBody
    public ApiResult<?> ruleAdd(@RequestBody @Validated CrawlerRuleEditInDto crawlerRuleEditInDto){
        CrawlerRule crawlerRule = crawlerRuleService.saveRule(crawlerRuleEditInDto);
        if(null != crawlerRule){
            return ApiResult.ok(crawlerRule.getRuleId());
        }
        return ApiResult.fail("save fail");
    }


    @PostMapping("/ruleTest")
    @ResponseBody
    public  ApiResult<?> ruleTest(@RequestBody @Validated CrawlerRuleEditInDto crawlerRuleEditInDto){
        String testResult = crawlerRuleService.ruleTest(crawlerRuleEditInDto);
        log.info(testResult);
        return ApiResult.ok(testResult);
    }


    @PostMapping("/xpathTest")
    @ResponseBody
    public  ApiResult<?> xpathTest(@RequestBody @Validated XpathTestInDto xpathTestInDto){
        XpathTestOutDto xpathTestOutDto = crawlerRuleService.xpathTest(xpathTestInDto);
        log.info("test result:{}",xpathTestOutDto);
        return ApiResult.ok(xpathTestOutDto);
    }


    @PostMapping("/getAllRule")
    @ResponseBody
    public ApiResult<?> getAllRule(){
        List<CrawlerRule> allRules = crawlerRuleService.getAll();
        return ApiResult.ok(allRules);
    }


    @PostMapping("/delRule")
    @ResponseBody
    public ApiResult<?> delRule(@RequestParam String ruleId){
        if(StrUtil.isBlank(ruleId)){
            return ApiResult.fail("ruleId不能为空");
        }
        crawlerRuleService.delRule(ruleId);
        return ApiResult.ok("success");
    }

    @PostMapping("/closeRule")
    @ResponseBody
    public ApiResult<?> closeRule(@RequestParam String ruleId){
        if(StrUtil.isBlank(ruleId)){
            return ApiResult.fail("ruleId不能为空");
        }
        crawlerRuleService.enableRule(ruleId,0);
        return ApiResult.ok("success");
    }

    @PostMapping("/openRule")
    @ResponseBody
    public ApiResult<?> openRule(@RequestParam String ruleId){
        if(StrUtil.isBlank(ruleId)){
            return ApiResult.fail("ruleId不能为空");
        }
        crawlerRuleService.enableRule(ruleId,1);
        return ApiResult.ok("success");
    }


}
