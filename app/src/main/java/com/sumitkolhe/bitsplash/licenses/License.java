package com.sumitkolhe.bitsplash.licenses;

import com.sumitkolhe.bitsplash.items.InAppBilling;

public class License {

	/*
	 * License Checker
	 * private static final boolean ENABLE_LICENSE_CHECKER = true; --> enabled
	 * Change to private static final boolean ENABLE_LICENSE_CHECKER = false; if you want to disable it
	 *
	 * NOTE: If you disable license checker you need to remove LICENSE_CHECK permission inside AndroidManifest.xml
	 */
	private static final boolean ENABLE_LICENSE_CHECKER = false;

	/*
	 * NOTE: If license checker is disabled (above), just ignore this
	 *
	 * Generate 20 random bytes
	 * For easy way, go to https://www.random.org/strings/
	 * Set generate 20 random strings
	 * Each string should be 2 character long
	 * Check numeric digit (0-9)
	 * Choose each string should be unique
	 * Get string
	 */
	private static final byte[] SALT = new byte[]{
			//Put generated random bytes here, separate with comma
			//Ex: 14, 23, 58, 85, ...
			//83, 27, 11, 17, 73, 76, 85, 38, 78, 22, 81, 65, 76, 75, 41, 60, 77 ,79 ,82 ,74
	};

	/*
	 * Your license key
	 * If your app hasn't published at play store, publish it first as beta, get license key
	 */
	private static final String LICENSE_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAsKlctE2yd40I3Od/Zvg90Lrx4XYP8ipk/JVCCx2ZUv8MfF8y4pUI/AFd8suqpJOq7iy6LNZONBOWXbKyXDnB563tVhNIlE19sPErts3PMuaFmPMwCkIvkn3s8vJqi8hW1BoeNJNUoDQyfTPIiIQJexmlpfFVeHvC1BlUJK/DM0bvc14Bq+96dKeLN7azN1s7yvWdpSK2+0Cj1JEvre7rhLxktsPQitI4k8BG0bmkcGHUCYN6hIRD9lIXWWROUrSbKAcgn3Ttw2r8+mYg3Rgdqndy92JBkbdEpAhUZfkJLnqg3rspg7hF7yCq/Frj7J112SXie5CNSbFTvyDh/ZL2KQIDAQAB";

	/*
	 * NOTE: Make sure your app name in project same as app name at play store listing
	 * NOTE: Your InApp Purchase will works only after the apk published
	 */

	/*
	 * NOTE: If donation disabled, just ignored this
	 *
	 * InApp product id for donation
	 * Product name displayed the same as product name displayed at play store
	 * So make sure to name it properly
	 * Format: new InAppBilling("donation product id")
	 */
	private static final InAppBilling[] DONATION_PRODUCTS = new InAppBilling[] {
			new InAppBilling("com.sumitkolhe.bitsplash.coffee"),
			new InAppBilling("com.sumitkolhe.bitsplash.burger"),
			new InAppBilling("com.sumitkolhe.bitsplash.pizza"),
			new InAppBilling("com.sumitkolhe.bitsplash.server")
	};

	public static boolean isLicenseCheckerEnabled() {
		return ENABLE_LICENSE_CHECKER;
	}

	public static String getLicenseKey() {
		return LICENSE_KEY;
	}

	public static byte[] getRandomString() {
		return SALT;
	}

	public static String[] getDonationProductsId() {
		String[] productId = new String[DONATION_PRODUCTS.length];
		for (int i = 0; i < DONATION_PRODUCTS.length; i++) {
			productId[i] = DONATION_PRODUCTS[i].getProductId();
		}
		return productId;
	}

}