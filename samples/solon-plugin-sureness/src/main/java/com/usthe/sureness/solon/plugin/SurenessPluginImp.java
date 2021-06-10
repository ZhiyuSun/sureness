package com.usthe.sureness.solon.plugin;

import com.usthe.sureness.solon.plugin.integration.SurenessConfiguration;
import com.usthe.sureness.subject.SubjectSum;
import com.usthe.sureness.util.JsonWebTokenUtil;
import com.usthe.sureness.util.SurenessContextHolder;
import org.noear.solon.SolonApp;
import org.noear.solon.core.Plugin;
import org.noear.solon.core.handle.RenderManager;

import java.util.List;
import java.util.UUID;

/**
 * @author noear
 * @author tomsun28
 */
public class SurenessPluginImp implements Plugin {
    @Override
    public void start(SolonApp app) {
        app.beanScan(SurenessConfiguration.class);

        // issue jwt rest api
        app.get("/auth/token", ctx -> {
            SubjectSum subjectSum = SurenessContextHolder.getBindSubject();

            if (subjectSum == null) {
                RenderManager.global.render("Please auth!", ctx);
            } else {
                String principal = (String) subjectSum.getPrincipal();
                List<String> roles = (List<String>) subjectSum.getRoles();
                // issue jwt
                String jwt = JsonWebTokenUtil.issueJwt(UUID.randomUUID().toString(), principal,
                        "token-server", 3600L, roles);
                RenderManager.global.render(jwt, ctx);
            }
        });

        app.after(ctx -> SurenessContextHolder.unbindSubject());
    }
}
