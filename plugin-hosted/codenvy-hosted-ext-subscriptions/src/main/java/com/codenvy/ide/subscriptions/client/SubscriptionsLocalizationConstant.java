/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 *  [2012] - [2015] Codenvy, S.A.
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
package com.codenvy.ide.subscriptions.client;

import com.google.gwt.i18n.client.Messages;

/**
 * The localization constants for the subscriptions extension.
 *
 * @author Kevin Pollet
 */
public interface SubscriptionsLocalizationConstant extends Messages {

    /*
     * Queue Type
     */
    @Key("queue.type.indicator.tooltip.shared.title")
    String queueTypeTooltipSharedTitle();

    @Key("queue.type.indicator.tooltip.shared.message")
    String queueTypeTooltipSharedMessage();

    @Key("queue.type.indicator.tooltip.dedicated.title")
    String queueTypeTooltipDedicatedTitle();

    @Key("queue.type.indicator.tooltip.dedicated.message")
    String queueTypeTooltipDedicatedMessage(int nbQueues);
}
