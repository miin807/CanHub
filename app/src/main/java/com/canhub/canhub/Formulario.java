package com.canhub.canhub;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class Formulario extends AppCompatActivity {
    private ViewPagerAdapter viewPagerAdapter;
    private ViewPager2 viewPager;
    private TabLayout tabLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_formulario);

        viewPager = findViewById(R.id.viewpager);
        tabLayout = findViewById(R.id.tab_layout);

        //el adaptador
        viewPagerAdapter = new ViewPagerAdapter(this);

        viewPagerAdapter.addFragment(new Formulariopt1(),"Formulario pt1");
        viewPagerAdapter.addFragment(new Formulariopt2(),"Formulario pt2");
        viewPagerAdapter.addFragment(new Formulariopt3(),"Formulario pt3");

        viewPager.setAdapter(viewPagerAdapter);

        new TabLayoutMediator(tabLayout,viewPager,((tab, position) -> tab.setText(viewPagerAdapter.getPageTitle(position)))).attach();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}