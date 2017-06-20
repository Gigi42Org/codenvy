/*
 *  [2012] - [2017] Codenvy, S.A.
 *  All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Codenvy S.A. and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Codenvy S.A.
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Codenvy S.A..
 */
package com.codenvy.api.workspace.server.filters;

import com.codenvy.api.permission.server.filter.check.DefaultSetPermissionsChecker;
import com.codenvy.api.permission.server.filter.check.SetPermissionsChecker;
import com.codenvy.api.permission.shared.model.Permissions;

import org.eclipse.che.api.core.ForbiddenException;
import org.eclipse.che.commons.env.EnvironmentContext;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.HashSet;
import java.util.Set;

import static com.codenvy.api.permission.server.SystemDomain.DOMAIN_ID;
import static com.codenvy.api.permission.server.SystemDomain.MANAGE_SYSTEM_ACTION;
import static com.codenvy.api.workspace.server.stack.StackDomain.READ;
import static com.codenvy.api.workspace.server.stack.StackDomain.SEARCH;
import static com.codenvy.api.workspace.server.stack.StackDomain.getActions;
import static java.util.stream.Collectors.toList;

/**
 * Stack domain specific set permission checker.
 *
 * @author Anton Korneta
 */
@Singleton
public class StackDomainSetPermissionsChecker implements SetPermissionsChecker {

    private final DefaultSetPermissionsChecker defaultChecker;

    @Inject
    public StackDomainSetPermissionsChecker(DefaultSetPermissionsChecker defaultChecker) {
        this.defaultChecker = defaultChecker;
    }

    @Override
    public void check(Permissions permissions) throws ForbiddenException {
        if (!"*".equals(permissions.getUserId())) {
            defaultChecker.check(permissions);
            return;
        }
        final Set<String> unsupportedPublicActions = new HashSet<>(permissions.getActions());
        unsupportedPublicActions.remove(READ);

        //public search is supported only for admins
        if (EnvironmentContext.getCurrent().getSubject().hasPermission(DOMAIN_ID, null, MANAGE_SYSTEM_ACTION)) {
            unsupportedPublicActions.remove(SEARCH);
        } else {
            defaultChecker.check(permissions);
        }

        if (!unsupportedPublicActions.isEmpty()) {
            throw new ForbiddenException("Following actions are not supported for setting as public:" +
                                         getActions().stream()
                                                     .filter(a -> !(a.equals(READ) || a.equals(SEARCH)))
                                                     .collect(toList()));
        }
    }

}
