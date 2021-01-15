package com.techversant.eveara.ui.signup

import android.os.Bundle
import android.text.TextUtils
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.digicom.onesqft.base.BaseViewModel
import com.techversant.eveara.common.Constants
import com.techversant.eveara.model.request.Address
import com.techversant.eveara.model.request.UserSignUpRequest
import com.techversant.eveara.utils.CodeSnippetExtension
import com.techversant.eveara.webservice.base.NetworkResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

class SignupViewModel : BaseViewModel() {
    val signUpResponse = MutableLiveData<NetworkResult<Any>?>()
    val countryListResponse = MutableLiveData<NetworkResult<Any>?>()
    val languageListResponse = MutableLiveData<NetworkResult<Any>?>()
    val stateListResponse = MutableLiveData<NetworkResult<Any>?>()
    var userFirstName: String = ""
    var userGender: String = "F"
    var userLanguageCode: String = ""
    var userStateIso: String = ""
    var userSurName: String = ""
    var userMailId: String = ""
    var userPassword: String = ""
    var userCountryIso: String = ""
    var userConfirmPassword: String = ""
    var userTaxPaying: String = "1"
    private val signUpRepository: SignUpRepository by lazy { SignUpRepository(CodeSnippetExtension.getInstance()) }
    override fun onCreate(bundle: Bundle?) {
        getCountryList()
        getLanguageList()
    }

    private fun getCountryList() {
        viewModelScope.launch {
            signUpRepository.getCountryList(sharedPreferences.token, countryListResponse)
        }
    }

    private fun getLanguageList() {
        viewModelScope.launch {
            signUpRepository.getLanguageList(sharedPreferences.token, languageListResponse)
        }
    }
    /**
     * Validate Email address
     * @return true if validated success otherwise false
     */
    fun validateEmail(): Boolean {
        if (userMailId.isBlank()) {
            return false
        } else {
            return userMailId.isEmailValid()
        }
    }
    private fun String.isEmailValid(): Boolean {
        return !TextUtils.isEmpty(this) && android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()
    }
    fun userSignUp() {
        viewModelScope.launch(Dispatchers.IO) {
            runCatching {
                signUpRepository.signUpUser(getSignUpRequest(), signUpResponse)
            }.onSuccess { repo ->

            }.onFailure {
                Timber.d(it)
                it.printStackTrace()
            }
        }
    }

    private fun getSignUpRequest(): UserSignUpRequest {
        return UserSignUpRequest(
            Address(),
            Constants.CLIENT_ID,
            userTaxPaying,
            userMailId,
            userFirstName,
            userGender,
            userLanguageCode,
            userPassword,
            userSurName,
            userCountryIso,
            userStateIso
        )
    }

    fun getStateList(iso: String) {
        viewModelScope.launch {
            signUpRepository.getStateList(sharedPreferences.token, iso, stateListResponse)
        }
    }

    fun isValidPassword() : Boolean {
        userPassword?.let {
            val passwordPattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,16}$"
            val passwordMatcher = Regex(passwordPattern)

            return passwordMatcher.find(userPassword) != null
        }
    }
}
