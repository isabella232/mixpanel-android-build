package com.mixpanel.android.compile;

import com.mixpanel.android.build.Tweak;

import java.util.Collection;

import javax.lang.model.element.Name;


public class TweakClassWriter {
    public String tweaksClassAsString(Name packageName, Collection<AppliedTweak> appliedTweaks) {
        final StringBuilder ret = new StringBuilder();
        final String header = String.format(CLASS_HEADER_TEMPLATE, packageName);
        ret.append(header);

        for (final AppliedTweak application:appliedTweaks) {
            final Tweak tweak = application.getTweak();
            final AppliedTweak.ParameterType parameterType = application.getParameterType();
            final String block = String.format(
                    CLASS_BODY_TEMPLATE,
                    application.getTweakedType().getQualifiedName(),
                    tweak.name(),
                    parameterType.getFormattedDefaultValue(tweak),
                    parameterType.getTypeName(),
                    application.getTweakedMethod().getSimpleName()
            );
            ret.append(block);
        }

        ret.append(CLASS_FOOTER);
        return ret.toString();
    }

    private static final String CLASS_HEADER_TEMPLATE =
        "package %1$s;\n" + // PACKAGE OF ENCLOSING CLASS
        "\n" +
        "import com.mixpanel.android.mpmetrics.Tweaks;\n" +
        "\n" +
        "public class $$TWEAK_REGISTRAR implements Tweaks.TweakRegistrar {\n" +
        "\n" +
        "    @Override\n" +
        "    public void registerObjectForTweaks(final Tweaks t, final Object registrant) {\n" +
        "\n";

    private static final String CLASS_BODY_TEMPLATE =
        "        {\n" +
        "            final String tweakName = \"%2$s\";\n" + // TWEAK NAME
        "            final String tweakDefault = %3$s;\n" + // DEFAULT VALUE -- QUOTED OR WHATEVER
        "\n" +
        "            final %1$s typedRegistrant = (%1$s) registrant;\n" + // TWEAKED CLASS, TWEAKED CLASS
        "            t.bind(TweakName, TweakDefault, new Tweaks.TweakChangeCallback() {\n" +
        "                @Override\n" +
        "                public void onChange(Object _ignored) {\n" +
        "                    final %4$s tweakValue = t.get%4$s(tweakName, tweakDefault);\n" + // PARAM TYPE, PARAM TYPE
        "                    typedRegistrant.%5$s(tweakValue);\n" + // TWEAKED METHOD NAME
        "                }\n" +
        "            }); // bind()\n" +
        "        }\n" +
        "\n";

    private static final String CLASS_FOOTER =
        "    } // registerObjectForTweaks\n" +
        "} // class\n";

}