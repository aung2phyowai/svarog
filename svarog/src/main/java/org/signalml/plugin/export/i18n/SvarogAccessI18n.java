package org.signalml.plugin.export.i18n;

import org.signalml.plugin.export.PluginAuth;

/**
 * i18n support for plugins.
 * 
 * @author Stanislaw Findeisen (Eisenbits)
 */
public interface SvarogAccessI18n {

    /**
     * Translates the message for the specified key using the current Svarog locale.
     * The message is fetched from local plugin i18n store first, and if not found,
     * global Svarog i18n store is used. 
     * 
     * @param auth plugin authentication object
     * @param catalogId plugin i18n catalog ID (resource bundle base name)
     * @param key English version of the message
     * @return i18n version of the message (depending on the current Svarog locale),
     *         or key if not found
     */
    public String translate(PluginAuth auth, String catalogId, String key);

    /**
     * Translates the message for the specified key using the current Svarog locale,
     * and renders it using actual values.
     * The message is fetched from local plugin i18n store first, and if not found,
     * global Svarog i18n store is used.
     * 
     * @param auth plugin authentication object
     * @param catalogId plugin i18n catalog ID (resource bundle base name)
     * @param key English version of the message
     * @param arguments the values to render
     * @return i18n version of the message (depending on the current Svarog locale),
     *         with arguments rendered in, or key if not found
     */
    public String translateR(PluginAuth auth, String catalogId, String key, Object ... arguments);

    /**
     * Translates the message for the specified key using the current Svarog locale.
     * The message is fetched from local plugin i18n store first, and if not found,
     * global Svarog i18n store is used. 
     * 
     * @param auth plugin authentication object
     * @param catalogId plugin i18n catalog ID (resource bundle base name)
     * @param key English version of the message
     * @param keyPlural English version of the message (plural form)
     * @param n tells "how many" and is used to select the correct plural form
     *        (there may be more than 2) 
     * @return i18n version of the message (depending on the current Svarog locale and n),
     *         or keyPlural if not found
     */
    public String translateN(PluginAuth auth, String catalogId, String key, String keyPlural, long n);

    /**
     * Translates the message for the specified key using the current Svarog locale,
     * and renders it using actual values.
     * The message is fetched from local plugin i18n store first, and if not found,
     * global Svarog i18n store is used. 
     * 
     * @param auth plugin authentication object
     * @param catalogId plugin i18n catalog ID (resource bundle base name)
     * @param key English version of the message
     * @param keyPlural English version of the message (plural form)
     * @param n tells "how many" and is used to select the correct plural form
     *        (there may be more than 2)
     * @param arguments the values to render
     * @return i18n version of the message (depending on the current Svarog locale and n),
     *         with arguments rendered in, or keyPlural if not found
     */
    public String translateNR(PluginAuth auth, String catalogId, String key, String keyPlural, long n, Object ... arguments);

	/**
	 * Just renders the given pattern using actual values.
	 * Pattern is used as-is (no translation!).
	 * 
	 * @param pattern message string with placeholders like {0}
	 * @param arguments values to render into the placeholders
	 * @return
	 * @see java.text.MessageFormat.format
	 */
	public String render(String pattern, Object ... arguments);
}