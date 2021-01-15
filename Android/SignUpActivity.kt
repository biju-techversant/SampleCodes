package com.techversant.eveara.ui.signup

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.observe
import com.digicom.onesqft.base.BaseActivity
import com.spotify.sdk.android.authentication.LoginActivity
import com.techversant.eveara.R
import com.techversant.eveara.adapter.CustomSpinnerAdapter
import com.techversant.eveara.databinding.ActivitySignUpBinding
import com.techversant.eveara.model.response.*
import com.techversant.eveara.ui.otp.OtpActivity

class SignUpActivity : BaseActivity<SignupViewModel, ActivitySignUpBinding>(),
    AdapterView.OnItemSelectedListener {
    private var initialRequiredWSCount = 2
    private lateinit var stateAdapter: CustomSpinnerAdapter
    private lateinit var countryAdapter: CustomSpinnerAdapter
    private lateinit var languageAdapter: CustomSpinnerAdapter
    private val stateList = ArrayList<Any>()
    private val countryList = ArrayList<Any>()
    private val languageList = ArrayList<Any>()

    override fun initializeView(): SignupViewModel {
        val viewModel: SignupViewModel by viewModels()
        return viewModel
    }

    override fun layoutRes(): Int {
        return R.layout.activity_sign_up
    }

    override fun initializeListener() {
        viewBinding.viewModel = viewModel
        showProgress()
        addWebserviceObservers()
    }


    private fun hideProgressIfAllCompleted() {
        initialRequiredWSCount--
        if (initialRequiredWSCount <= 0) {
            hideProgress()
        }
    }

    /**
     * Web service observers
     */
    private fun addWebserviceObservers() {
        viewModel.signUpResponse.observe(this) { result ->
            hideProgress()
            if (result != null) {
                val signUpResponse = viewModel.handleReponse(this, result) as UserSignUpResponse?
                if (signUpResponse?.success != null) {
                    val message =
                        if (!signUpResponse.success) getString(R.string.error) else signUpResponse.message
                    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
                    navigateToOtpActivity(signUpResponse.data.otpUuid)
                }
            }
        }
        viewModel.stateListResponse.observe(this) { result ->
            hideProgress()
            if (result != null) {
                val stateResponse = viewModel.handleReponse(this, result) as CountryStateResponse?
                if (stateResponse?.success != null && !stateResponse.data.isNullOrEmpty() && stateResponse.data.isNotEmpty()) {
                    val message =
                        if (!stateResponse.success) getString(R.string.error) else stateResponse.data
                    stateList.clear()
                    stateList.addAll(stateResponse.data)
                    stateList.add(
                        0,
                        StateData(getString(R.string.state_hint), getString(R.string.state_hint))
                    )
                    stateAdapter.notifyDataSetChanged()
                    setSelectionState()
                } else {
                    stateList.add(
                        0,
                        StateData(getString(R.string.state_hint), getString(R.string.state_hint))
                    )
                    stateAdapter.notifyDataSetChanged()
                    setSelectionState()
                }
            }
        }
        viewModel.countryListResponse.observe(this) { result ->
            hideProgressIfAllCompleted()
            if (result != null) {
                val countryResponse = viewModel.handleReponse(this, result) as CountryListResponse?
                if (countryResponse?.success != null && !countryResponse.data.isNullOrEmpty() && countryResponse.data.isNotEmpty()) {
                    countryList.clear()
                    countryList.addAll(countryResponse.data)
                    countryList.add(
                        0,
                        CountryData(
                            getString(R.string.country_hint),
                            getString(R.string.country_hint),
                            ""
                        )
                    )
                    countryAdapter.notifyDataSetChanged()
                }
            }
        }
        viewModel.languageListResponse.observe(this) { result ->
            hideProgressIfAllCompleted()
            if (result != null) {
                val languageResponse =
                    viewModel.handleReponse(this, result) as LanguageListResponse?
                if (languageResponse?.success != null && !languageResponse.data.isNullOrEmpty() && languageResponse.data.isNotEmpty()) {
                    val message =
                        if (!languageResponse.success) getString(R.string.error) else languageResponse.data
                    languageList.clear()
                    languageList.addAll(languageResponse.data)
                    languageList.add(
                        0,
                        LanguageData(
                            getString(R.string.language_hint),
                            getString(R.string.language_hint)
                        )
                    )
                    languageAdapter.notifyDataSetChanged()
                }
            }
        }
        setCountryAdapter()
        setLanguageAdapter()
        setStateAdapter()
    }

    private fun setSelectionState() {
        var found = false
        for ((position, i) in stateList.withIndex()) {
            i as StateData
            if (i.iso == viewModel.userStateIso) {
                found = true
                viewBinding.spState.setSelection(position)
            }
        }
        if (!found) {
            viewBinding.spState.setSelection(0)
        }
    }

    private fun navigateToOtpActivity(otpUuid: String) {
        var bundle = Bundle()
        bundle.putString("otpUUID", otpUuid)
        bundle.putString("otpevent", "SIGNUP")
        navigateTo(OtpActivity::class.java, false, bundle)
    }

    private fun setStateAdapter() {
        stateAdapter = CustomSpinnerAdapter(this, stateList)
        viewBinding.spState.adapter = stateAdapter
        viewBinding.spState.onItemSelectedListener = this
    }

    private fun setCountryAdapter() {
        countryAdapter = CustomSpinnerAdapter(this, countryList)
        viewBinding.spCountry.adapter = countryAdapter
        viewBinding.spCountry.onItemSelectedListener = this
    }

    private fun setLanguageAdapter() {
        languageAdapter = CustomSpinnerAdapter(this, languageList)
        viewBinding.spLanguage.adapter = languageAdapter
        viewBinding.spLanguage.onItemSelectedListener = this
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {

    }

    override fun onItemSelected(
        adapterView: AdapterView<*>?,
        view: View?,
        position: Int,
        id: Long
    ) {
        when (adapterView?.id) {
            viewBinding.spCountry.id -> {
                val item = adapterView.getItemAtPosition(position) as CountryData
                viewBinding.tvCountry.text = item.countryName
                if (item.countryName != getString(R.string.country_hint)) {
                    viewModel.userCountryIso = item.iso
                    viewBinding.tvCountry.error = null
                    viewBinding.tvState.error = null
                }
                showProgress()
                stateList.clear()
                viewModel.getStateList(item.iso)
            }
            viewBinding.spLanguage.id -> {
                val item = adapterView.getItemAtPosition(position) as LanguageData
                if (item.languageName != getString(R.string.language_hint)) {
                    viewModel.userLanguageCode = item.languageCode
                    viewBinding.tvLanguage.error = null

                }
                viewBinding.tvLanguage.text = item.languageName
            }
            viewBinding.spState.id -> {
                val item = adapterView.getItemAtPosition(position) as StateData
                if (item.stateName != getString(R.string.state_hint)) {
                    viewModel.userStateIso = item.iso
                } else {
                    viewModel.userStateIso = ""
                }
                viewBinding.tvState.text = item.stateName
            }
        }
    }

    fun showCountryList(view: View) {
        if (countryList.size == 1 || countryList.size == 0) {
            Toast.makeText(this, "Empty state list", Toast.LENGTH_LONG).show()
        }
        viewBinding.spCountry.performClick()
    }

    fun showStateList(view: View) {
        if (stateList.size == 0 || stateList.size == 1) {
            Toast.makeText(this, "Empty state list", Toast.LENGTH_LONG).show()
        }
        viewBinding.spState.performClick()
    }

    fun showLanguageList(view: View) {
        if (languageList.size == 0 || languageList.size == 1) {
            Toast.makeText(this, "Empty state list", Toast.LENGTH_LONG).show()
        }
        viewBinding.spLanguage.performClick()
    }

    fun navigateToLoginActivity(view: View) {
        navigateTo(LoginActivity::class.java, true)
    }

    fun userSignUpAction(view: View) {
        var isValid = true
        if (viewModel.userLanguageCode.isBlank() || viewModel.userStateIso.isBlank() || viewModel.userCountryIso.isBlank() || viewModel.userConfirmPassword.isBlank() || viewModel.userPassword.isBlank() || viewModel.userMailId.isBlank() || viewModel.userFirstName.isBlank() || viewModel.userSurName.isBlank() || viewModel.userMailId.isBlank()) {
            isValid = false
        }

        if (!isValid) {
            warningDialog(getString(R.string.mandatory_fields))
        } else {
            if (!viewModel.validateEmail()) {
                warningDialog(getString(R.string.invalid_email_id))
                isValid = false
            } else if (!viewModel.isValidPassword()) {
                warningDialog("User password "+getString(R.string.password_validation))
                isValid = false
            } else if ((viewModel.userPassword != viewModel.userConfirmPassword)) {
                warningDialog(getString(R.string.password_mismatch))
                isValid = false
            } else if (!viewBinding.cbTermsAndCondition.isChecked) {
                warningDialog("Please accept terms and condition")
                isValid = false
            }
        }
        if (isValid) {
            showProgress()
            viewModel.userSignUp()
        }
    }


}
