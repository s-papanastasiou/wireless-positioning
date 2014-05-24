package me.gregalbiston.androidknn.display;

import android.content.Context;
import android.preference.ListPreference;
import android.util.AttributeSet;

/**
 * Created with IntelliJ IDEA.
 * User: Gerg
 * Date: 28/07/13
 * Time: 18:39
 * Based on Michael answer on Aug 10 '11: http://stackoverflow.com/questions/7017082/change-the-summary-of-a-listpreference-with-the-new-value-android -
 */
public class SummaryListPreference extends ListPreference {

    public SummaryListPreference(final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public CharSequence getSummary() {
        final CharSequence entry = getEntry();
        final CharSequence summary = super.getSummary();
        if (summary == null || entry == null) {
            return null;
        } else {
            return String.format(summary.toString(), entry);
        }
    }

    @Override
    public void setValue(final String value) {
        super.setValue(value);
        notifyChanged();
    }

}
