package com.absir.aserv.system.admin;

import com.absir.aserv.crud.ICrudSupply;
import com.absir.aserv.jdbc.JdbcCondition;
import com.absir.aserv.jdbc.JdbcPage;
import com.absir.aserv.menu.value.MaPermission;
import com.absir.aserv.system.bean.proxy.JiUserBase;
import com.absir.aserv.system.bean.value.JeRoleLevel;
import com.absir.aserv.system.crud.RichCrudFactory;
import com.absir.aserv.system.crud.UploadCrudFactory;
import com.absir.aserv.system.dao.BeanDao;
import com.absir.aserv.system.dao.utils.QueryDaoUtils;
import com.absir.aserv.system.helper.HelperString;
import com.absir.aserv.system.security.SecurityContext;
import com.absir.aserv.system.service.AuthService;
import com.absir.aserv.system.service.BeanService;
import com.absir.aserv.system.service.SecurityService;
import com.absir.aserv.system.service.statics.EntityStatics;
import com.absir.aserv.system.service.utils.AccessServiceUtils;
import com.absir.aserv.system.service.utils.InputServiceUtils;
import com.absir.aserv.transaction.TransactionIntercepter;
import com.absir.bean.basis.Base;
import com.absir.core.helper.HelperFileName;
import com.absir.core.kernel.KernelDyna;
import com.absir.core.kernel.KernelLang;
import com.absir.core.kernel.KernelString;
import com.absir.server.exception.ServerException;
import com.absir.server.exception.ServerStatus;
import com.absir.server.in.InMethod;
import com.absir.server.in.InModel;
import com.absir.server.in.Input;
import com.absir.server.value.*;
import com.absir.servlet.InputRequest;
import org.apache.commons.fileupload.FileItem;
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
     * 上传文件
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
    public String ue(InputRequest inputRequest) throws IOException {
        String action = inputRequest.getParam("action");
        if (!KernelString.isEmpty(action)) {
            if (action.equals("config")) {
                return "admin/ue.config";

            } else if (action.startsWith("upload")) {
                // 检测上传权限
                JiUserBase userBase = SecurityService.ME.getUserBase(inputRequest);
                if (!AuthService.ME.menuPermission(RichCrudFactory.UPLOAD, userBase)) {
                    throw new ServerException(ServerStatus.ON_DENIED);
                }

                FileItem fileItem = UploadCrudFactory.getUploadFile(inputRequest, "upfile");
                if (fileItem != null) {
                    String fileType = action.substring("upload".length());
                    String uploadFile = UploadCrudFactory.ME.uploadExtension(fileType, -1, HelperFileName.getExtension(fileItem.getName()).toLowerCase(), fileItem.getInputStream(), userBase);
                    inputRequest.getModel().put("file", uploadFile);
                    return "admin/ue.upload";
                }

            } else if (action.startsWith("list")) {
                String fileType = action.substring("list".length());
                JdbcPage jdbcPage = new JdbcPage();
                jdbcPage.setPageSize(KernelDyna.to(inputRequest.getParam("size"), int.class));
                jdbcPage.setPageIndex(KernelDyna.to(inputRequest.getParam("start"), int.class) / jdbcPage.getPageSize() + 1);
                TransactionIntercepter.open(inputRequest, BeanService.ME.getTransactionName(), BeanService.TRANSACTION_READ_ONLY);
                Object list = QueryDaoUtils.selectQuery(BeanDao.getSession(), "JUpload", null, null, new Object[]{"o.dirPath like ?", fileType + "/%", "o.fileType IS NOT NULL", KernelLang.NULL_OBJECT}, "ORDER BY o.id DESC", jdbcPage);
                inputRequest.getModel().put("uploads", list);
                inputRequest.getModel().put("jdbcPage", jdbcPage);
                return "admin/ue.list";
            }
        }

        return "ue.error";
    }
}
