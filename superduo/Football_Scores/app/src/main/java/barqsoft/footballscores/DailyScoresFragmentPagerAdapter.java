package barqsoft.footballscores;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

import org.joda.time.LocalDate;

/**
 * Created by Andrés on 9/11/15.
 */
class DailyScoresFragmentPagerAdapter extends FragmentStatePagerAdapter {

    //Constants
    private static final String LOG_TAG = DailyScoresFragmentPagerAdapter.class.getSimpleName();
    private static final int NUM_PAGES = 5;
    private static final boolean DEBUG = true;

    //Variables
    private final Context mContext;

    public DailyScoresFragmentPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
        LocalDate mLocalDate = new LocalDate();
    }

    @Override
    public Fragment getItem(int position) {
        LocalDate localDateForItem = getLocalDateForPosition(position);
        long millis = localDateForItem.toDateTimeAtStartOfDay().getMillis();

        return FixturesFragment.newInstance(millis);
    }

    @Override
    public int getCount() {
        return NUM_PAGES;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch(position) {
            case 1:
                return mContext.getString(R.string.yesterday);

            case 2:
                return mContext.getString(R.string.today);

            case 3:
                return mContext.getString(R.string.tomorrow);

            default:
                LocalDate localDateForPosition = getLocalDateForPosition(position);
                return localDateForPosition.dayOfWeek().getAsText();
        }
    }

    private LocalDate getLocalDateForPosition(int position) {
        LocalDate localDate = new LocalDate();
        localDate = localDate.plusDays(position - 2);

        if(DEBUG)
            Log.d(LOG_TAG, "Position: " + position + " / " + localDate.toString());

        return localDate;
    }
}
