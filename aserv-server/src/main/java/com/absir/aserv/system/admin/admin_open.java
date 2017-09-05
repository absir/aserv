package com.absir.aserv.system.admin;

import com.absir.aserv.crud.ICrudSupply;
import com.absir.aserv.jdbc.JdbcCondition;
import com.absir.aserv.jdbc.JdbcPage;
import com.absir.aserv.menu.value.MaPermission;
import com.absir.aserv.system.bean.value.JeRoleLevel;
import com.absir.aserv.system.crud.RichCrudFactory;
import com.absir.aserv.system.crud.UploadCrudFactory;
import com.absir.aserv.system.helper.HelperString;
import com.absir.aserv.system.security.SecurityContext;
import com.absir.aserv.system.service.BeanService;
import com.absir.aserv.system.service.SecurityService;
import com.absir.aserv.system.service.statics.EntityStatics;
import com.absir.aserv.system.service.utils.AccessServiceUtils;
import com.absir.aserv.system.service.utils.InputServiceUtils;
import com.absir.aserv.transaction.TransactionIntercepter;
import com.absir.bean.basis.Base;
import com.absir.core.kernel.KernelString;
import com.absir.server.in.InMethod;
import com.absir.server.in.InModel;
import com.absir.server.in.Input;
import com.absir.server.value.*;
import com.absir.servlet.InputRequest;
import org.apache.commons.fileupload.FileUploadException;

import java.io.IOException;

/**
 * Created by absir on 2016/12/27.
 */
@Mapping("/admin")
@Base
@Server
public class admin_open extends AdminServer {

    @Override
    protected SecurityContext onAuthentication(Input input) throws Exception {
        SecurityContext securityContext = SecurityService.ME.autoLogin("admin", true, JeRoleLevel.ROLE_ADMIN.ordinal(), input);
        if (securityContext == null) {
            SecurityService.ME.autoLogin("api", true, JeRoleLevel.ROLE_ADMIN.ordinal(), input);
        }

        return securityContext;
    }

    /**
     * 弹出列表
     */
    public void suggest(String entityName, Input input) {
        ICrudSupply crudSupply = getCrudSupply(entityName, input);
        suggest(entityName, crudSupply, input);
        JdbcCondition condition = null;
        String param = input.getParam("@param");
        if (!KernelString.isEmpty(param)) {
            condition = new JdbcCondition();
            condition.addConditions(HelperString.split(param, "=&"));
        }

        TransactionIntercepter.open(input, crudSupply.getTransactionName(), BeanService.TRANSACTION_READ_ONLY);
        input.getModel().put("entities", EntityStatics.suggestCondition(entityName, InputServiceUtils.getSearchCondition(entityName, crudSupply.getEntityClass(entityName), null, condition, input), input));
    }

    public void lookup(String entityName, Input input) {
        lookup(entityName, null, input);
    }

    /**
     * 查找页面
     */
    @Mapping(method = InMethod.POST)
    public void lookup(String entityName, @Binder JdbcPage jdbcPage, Input input) {
        ICrudSupply crudSupply = getCrudSupply(entityName, input);
        suggest(entityName, crudSupply, input);
        JdbcCondition condition = null;
        String param = input.getParam("@param");
        if (!KernelString.isEmpty(param)) {
            condition = new JdbcCondition();
            condition.addConditions(HelperString.split(param, "=&"));
        }

        JdbcCondition jdbcCondition = AccessServiceUtils.suggestCondition(entityName, SecurityService.ME.getUserBase(input),
                InputServiceUtils.getSearchCondition(entityName, crudSupply.getEntityClass(entityName), null, condition, input));
        jdbcPage = InputServiceUtils.getJdbcPage(entityName, jdbcPage, input);
        InModel model = input.getModel();
        model.put("page", jdbcPage);

        TransactionIntercepter.open(input, crudSupply.getTransactionName(), BeanService.TRANSACTION_READ_ONLY);
        model.put("entities", crudSupply.list(entityName, jdbcCondition, InputServiceUtils.getOrderQueue(entityName, input), jdbcPage));
    }

    /**
     * 上传图片
     */
    @MaPermission(RichCrudFactory.UPLOAD)
    public void upload(InputRequest inputRequest) throws IOException, FileUploadException {
        inputRequest.getModel().put("paths", UploadCrudFactory.ME.uploads(SecurityService.ME.getUserBase(inputRequest), inputRequest.getRequest()));
    }

    /**
     * 文件列表
     */
    public void filemanager(@Nullable @Param String path, @Nullable @Param String order, Input input) {
        input.getModel().put("path", path);
        input.getModel().put("files", UploadCrudFactory.ME.list(path, order));
    }

    /**
     * UE编辑器后端支持
     */
    public String ue(InputRequest inputRequest) {
        String action = inputRequest.getParam("action");
        if (!KernelString.isEmpty(action)) {
            if (action.equals("config")) {
                return "admin/ue.config";
            }
        }

        return "ue.error";
    }
}
