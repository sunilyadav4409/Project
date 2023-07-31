package com.jbhunt.finance.carrierpayment.autopay.feature;

import io.rollout.configuration.RoxContainer;
import io.rollout.flags.RoxFlag;

public class FeatureFlags implements RoxContainer {

    public final RoxFlag publishChargeUpdatesToBilling  = new RoxFlag(false);
    public final RoxFlag paperInvcAutoPayProcess  = new RoxFlag(false);


}
