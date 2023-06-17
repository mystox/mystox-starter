package tech.mystox.framework;

import org.springframework.context.ApplicationContext;
import tech.mystox.framework.common.util.SpringContextUtil;
import tech.mystox.framework.config.IaConf;
import tech.mystox.framework.core.IaContext;
import tech.mystox.framework.core.IaENV;
import tech.mystox.framework.service.IaOpera;
import tech.mystox.framework.service.Impl.IaOperaImpl;

/**
 * Created by mystox on 2023/4/26, 16:23.
 * company:
 * description:
 * update record:
 */
public class MainTest {
    public static void main(String[] args) {

        ApplicationContext applicationContext = SpringContextUtil.getApplicationContext();
        IaENV iaENV = new IaENV();
        IaConf iaConf = new IaConf(null, null);
        iaConf.setGroupCode("USTAR");
        iaConf.setRegisterType("");
        iaENV.build(iaConf);
        iaENV.createMsgScheduler();
        //创建ia

        IaContext iaContext = new IaContext(iaConf, iaENV);


        IaOpera iaOpera = new IaOperaImpl(iaContext);
        iaOpera.operaAsync("hello", "hello opera");
    }
}
