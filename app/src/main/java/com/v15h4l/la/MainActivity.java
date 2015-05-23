package com.v15h4l.la;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;

import com.v15h4l.la.Fragments.AboutUsFragment;
import com.v15h4l.la.Fragments.HomeFragment;
import com.v15h4l.la.Prefs.PreferenceActivity;

import java.util.ArrayList;
import java.util.List;

import br.liveo.interfaces.NavigationLiveoListener;
import br.liveo.navigationliveo.NavigationLiveo;

public class MainActivity extends NavigationLiveo implements NavigationLiveoListener {

	@Override
	public void onUserInformation() {

		this.mUserBackground.setImageResource(R.drawable.img_header);

/*		// Custom Header
		View mCustomHeader = getLayoutInflater().inflate(R.layout.custom_header_user, this.getListView(), false);
		ImageView imageView = (ImageView) mCustomHeader.findViewById(R.id.imageView);
		this.addCustomHeader(mCustomHeader); //This will add the new header and remove the default user header
*/
	}

	@Override
	public void onInt(Bundle savedInstanceState) {

		//Creation of the list items is here

		// set listener {required}
		this.setNavigationListener(this);

        if (savedInstanceState == null) {
            //First item of the position selected from the list
            this.setDefaultStartPositionNavigation(0);
        }

		// name of the list items
		List<String> mListNameItem = new ArrayList<>();
		mListNameItem.add(0, getString(R.string.alarms));
		mListNameItem.add(1, getString(R.string.about));;

		// icons list items
		List<Integer> mListIconItem = new ArrayList<>();
        mListIconItem.add(0, R.drawable.ic_clock);
        mListIconItem.add(1, R.drawable.ic_about);

		//If not please use the FooterDrawer use the setFooterVisible(boolean visible) method with value false
		this.setFooterInformationDrawer(R.string.action_settings, R.drawable.ic_settings);

		this.setNavigationAdapter(mListNameItem, mListIconItem);

	}

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_MENU)
            return false;
        return super.onKeyDown(keyCode, event);
    }

    @Override
	public void onItemClickNavigation(int position, int layoutContainer) {

        switch (position){
            case 0:
                getFragmentManager().beginTransaction().replace(R.id.container, new HomeFragment()).commit();
            break;

            case 1:
                getFragmentManager().beginTransaction().replace(R.id.container, new AboutUsFragment()).commit();
                break;
        }
	}

	@Override
	public void onPrepareOptionsMenuNavigation(Menu menu, int position, boolean visible) {

	}

	@Override
	public void onClickFooterItemNavigation(View view) {
        //footer onClick
        startActivity(new Intent(this, PreferenceActivity.class));
    }

	@Override
	public void onClickUserPhotoNavigation(View view) {

	}
}
