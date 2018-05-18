package org.onap.mso.adapters.multivim;

public class MultiVimConstants {
  private MultiVimConstants(){

  }

  public static final String MSO_PROP_MULTIVIM_ADAPTER = "MSO_PROP_MULTIVIM_ADAPTER";
  public static final String MSB_ENDPOINT_URL = "org.onap.mso.adapters.multivim.msb.endpoint.url";
  public static final String POLL_INTERVAL = "org.onap.mso.adapters.multivim.create.pollInterval";
  public static final String POLL_TIMEOUT = "org.onap.mso.adapters.multivim.create.pollTimeout";
  public static final String CSAR_DIR = System.getProperty ("mso.config.path") + "ASDC/";
}
