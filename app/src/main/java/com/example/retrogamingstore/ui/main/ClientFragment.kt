package com.example.retrogamingstore.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.retrogamingstore.R
import com.google.android.material.bottomnavigation.BottomNavigationView

class ClientFragment : Fragment() {
    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_client, container, false)

        val bottomNavigationView = view.findViewById<BottomNavigationView>(R.id.bottom_navigation)
        val navHostFragment = childFragmentManager
            .findFragmentById(R.id.client_nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.profileFragment, R.id.orderHistoryFragment, R.id.orderDetailsFragment -> bottomNavigationView.menu.findItem(R.id.profileFragment).isChecked = true
                R.id.homeFragment -> bottomNavigationView.menu.findItem(R.id.homeFragment).isChecked = true
                R.id.listFragment -> bottomNavigationView.menu.findItem(R.id.listFragment).isChecked = true
                R.id.cartFragment, R.id.checkoutFragment -> bottomNavigationView.menu.findItem(R.id.cartFragment).isChecked = true
            }
        }

        bottomNavigationView.setupWithNavController(navController)

        return view
    }
}