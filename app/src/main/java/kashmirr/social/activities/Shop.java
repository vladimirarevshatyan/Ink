package kashmirr.social.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;

import com.kashmirr.social.R;

import java.util.ArrayList;
import java.util.List;

import kashmirr.social.fragments.Packs;
import kashmirr.social.utils.Constants;
import kashmirr.social.utils.User;


public class Shop extends BaseActivity {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.shopTitle));

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.getTabAt(0).setIcon(R.drawable.pack_icon);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(Packs.create(), getString(R.string.packs_title));
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.coinsMenu) {
        } else if (item.getItemId() == R.id.myCoinsMenu) {

        } else if (item.getItemId() == R.id.buyCoinsMenu) {
            startActivityForResult(new Intent(getApplicationContext(), BuyCoins.class), Constants.BUY_COINS_REQUEST_CODE);
        } else {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.shop_menu, menu);
        this.menu = menu;
        if (menu != null) {
            this.menu = menu;
            menu.findItem(R.id.myCoinsMenu).setTitle(getString(R.string.coinsText, User.get().getCoins()));
        }
        return true;
    }


    public void updateCoins() {
        if (menu != null) {
            menu.findItem(R.id.myCoinsMenu).setTitle(getString(R.string.coinsText, User.get().getCoins()));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case Constants.BUY_COINS_REQUEST_CODE:
                boolean coinsBought = data.getExtras().getBoolean(Constants.COINS_BOUGHT_KEY);
                if (coinsBought) {
                    updateCoins();
                }
                break;
        }
    }
}
