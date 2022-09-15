package com.parasoft.demoapp.util;

import android.widget.Toast;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.parasoft.demoapp.HomeActivity;
import com.parasoft.demoapp.R;

public class RefreshOrderUtil {
    public static void  refresh (HomeActivity activity) {
        SwipeRefreshLayout order_refresh = activity.findViewById(R.id.order_refresh);
        order_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                activity.loadOrderList();
                order_refresh.setRefreshing(false);
                Toast.makeText(activity, activity.isRefreshSuccess? R.string.refresh_successful:R.string.refresh_failed, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
