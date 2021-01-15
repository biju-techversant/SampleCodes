//
//  SignUpVC.swift
//  Eveara
//
//  Created by FCI-MA865 on 12/05/20.
//  Copyright © 2020 FCI-MA865. All rights reserved.
//

import UIKit

class SignUpVC: UITableViewController {
    
    @IBOutlet weak var viewHolderFirstName: UIView!
    @IBOutlet weak var viewHolderLanguage: UIView!
    @IBOutlet weak var txtFieldFirstName: UITextField!
    @IBOutlet weak var txtFieldLastName: UITextField!
    @IBOutlet weak var txtFieldEmail: UITextField!
    @IBOutlet weak var txtFieldPassword: UITextField!
    @IBOutlet weak var txtFieldConfirmPassword: UITextField!
    @IBOutlet weak var txtFieldCountry: UITextField! {
        didSet {
            txtFieldCountry.setRightViewWithSystemImage("chevron.down")
        }
    }
    @IBOutlet weak var txtFieldState: UITextField! {
        didSet {
            txtFieldState.setRightViewWithSystemImage("chevron.down")
        }
    }
    @IBOutlet weak var txtFieldLanguage: UITextField! {
        didSet {
            txtFieldLanguage.setRightViewWithSystemImage("chevron.down")
        }
    }
    @IBOutlet weak var btnYes: UIButton! {
        didSet {
            btnYes.isSelected = true
        }
    }
    @IBOutlet weak var btnNo: UIButton!
    @IBOutlet weak var btnTermsAndConditions: UIButton!
    @IBOutlet weak var labelTermsAndConditions: UILabel! {
        didSet {
            labelTermsAndConditions.attributedText = "I agree to Terms & Conditions".attributedStringWithColor("Terms & Conditions", highlightedColour: UIColor(named: "EVHyperLinkColor") ?? .blue)
        }
    }
    var taxPaying: String!
    var countryCode: String?
    var stateCode: String?
    var languageCode: String?
    
    override func viewDidLoad() {
        super.viewDidLoad()
        self.setupLabelTap()
        getCountryList()
        getLanguageList()
    }
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        self.navigationController?.navigationBar.isHidden = true
        self.tableView.backgroundImage = #imageLiteral(resourceName: "bg_Image")
        self.tableView.backgroundImageAlpha = 0.4
    }
    
    override func viewDidLayoutSubviews() {
        super.viewDidLayoutSubviews()
        viewHolderFirstName.roundCorners([.topLeft, .topRight], radius: 15.0)
        viewHolderLanguage.roundCorners([.bottomLeft, .bottomRight], radius: 15.0)
    }
    
    func getCountryList() {
        self.showHUD(message: "")
        APIClient().perform(GetCountry()) { (result) in
            self.hideHUD()
            switch result{
            case let .success(value):
                self.txtFieldCountry.pickerData = value.data
            case let .failure(error):
                print(error)
            }
        }
    }
    
    func getLanguageList() {
        self.showHUD(message: "")
        APIClient().perform(GetLanguage()) { (result) in
            self.hideHUD()
            switch result{
            case let .success(value):
                self.txtFieldLanguage.pickerData = value.data
            case let .failure(error):
                print(error)
            }
        }
    }
    
    func getStateListForCountry(_ countryISO: String)  {
        self.showHUD(message: "")
        APIClient().perform(GetState(
            queryParams: GetState.QueryParams(
                countryiso: countryISO
            ),
            body: nil)) { (result) in
                self.hideHUD()
                switch result {
                case let .success(value):
                    self.txtFieldState.pickerData = value.data
                case let .failure(error):
                    print(error)
                }
        }
    }
    
    func setupLabelTap() {
        let labelTap = UITapGestureRecognizer(target: self, action: #selector(self.labelTapped(_:)))
        self.labelTermsAndConditions.isUserInteractionEnabled = true
        self.labelTermsAndConditions.addGestureRecognizer(labelTap)
    }
    
    //MARK:- Button Action
    @IBAction func btnActionBack(_ sender: UIButton) {
        self.navigationController?.popViewController(animated: true)
    }
    
    @IBAction func btnActionYes(_ sender: UIButton) {
        if !(sender.isSelected) {
            sender.isSelected = !sender.isSelected
        }
        btnNo.isSelected = false
    }
    
    @IBAction func btnActionNo(_ sender: UIButton) {
        if !(sender.isSelected) {
            sender.isSelected = !sender.isSelected
        }
        btnYes.isSelected = false
    }
    
    @IBAction func btnAcceptTAndC(_ sender: UIButton) {
        sender.isSelected = !sender.isSelected
    }
    
    @objc func labelTapped(_ sender: UITapGestureRecognizer) {
        print("labelTapped")
    }
    
    @IBAction func btnActionSignUp(_ sender: UIButton) {
        view.endEditing(true)
        guard let firstName = txtFieldFirstName.text, !firstName.isEmpty else {
            alert(message: "Please enter First Name", title: "Mandatory Field")
            return
        }
        guard let lastName = txtFieldLastName.text, !lastName.isEmpty else {
            alert(message: "Please enter Last Name", title: "Mandatory Field")
            return
        }
        guard let email = txtFieldEmail.text, !email.isEmpty else {
            alert(message: "Please enter Email Address", title: "Mandatory Field")
            return
        }
        if !email.isValidEmail {
            alert(message: "Please provide a valid Email Address", title: "Email Validation")
            return
        }
        guard let password = txtFieldPassword.text, !password.isEmpty else {
            alert(message: "Please enter Password", title: "Mandatory Field")
            return
        }
        if !password.isValidPassword {
            alert(message: "Pasword must contain at least one uppercase letter, one lowercase letter, one numeric digit and at least 8 characters long", title: "Password Validation")
            return
        }
        guard let confirmPassword = txtFieldConfirmPassword.text, !confirmPassword.isEmpty else {
            alert(message: "Please enter Re-enter Password", title: "Mandatory Field")
            return
        }
        if password != confirmPassword {
            alert(message: "Password & Re-enter Password mis-match", title: "Password Validation")
            return
        }
        guard let country = txtFieldCountry.text, !country.isEmpty else {
            alert(message: "Please select a Country", title: "Mandatory Field")
            return
        }
        if let selectedCountry = txtFieldCountry.pickedData as? Country  {
            countryCode = selectedCountry.iso
        } else {
            alert(message: "Unable to fetch country iso", title: "Exception")
            return
        }
        guard let state = txtFieldState.text, !state.isEmpty else {
            alert(message: "Please select a State", title: "Mandatory Field")
            return
        }
        if let selectedState = txtFieldState.pickedData as? State  {
            stateCode = selectedState.iso
        } else {
            alert(message: "Unable to fetch state iso", title: "Exception")
            return
        }
        guard let language = txtFieldLanguage.text, !language.isEmpty else {
            alert(message: "Please select a Language", title: "Mandatory Field")
            return
        }
        if let selectedLanguage = txtFieldLanguage.pickedData as? Language  {
            languageCode = selectedLanguage.languageCode
        } else {
            alert(message: "Unable to fetch language code", title: "Exception")
            return
        }
        if btnTermsAndConditions.isSelected == false {
            alert(message: "Please accept the terms and conditions", title: "Terms & Conditions")
            return
        }
        if btnYes.isSelected {
            taxPaying = "1"
        } else {
            taxPaying = "0"
        }
        self.showHUD(message: "")
        APIClient().perform(SignUp(queryParams: nil,
                                   body: SignUp.Body(
                                    password: password,
                                    taxpaying: taxPaying,
                                    firstname: firstName,
                                    surname: lastName,
                                    email: email,
                                    gender: "M",
                                    address: SignUp.Body.Address(
                                        house: "",
                                        street: "",
                                        city: "",
                                        zip: "",
                                        mobile: ""),
                                    language: languageCode ?? "",
                                    country: countryCode ?? "",
                                    state: stateCode ?? ""))) { (result) in
                                        self.hideHUD()
                                        switch result {
                                        case .success(let value):
                                             DispatchQueue.main.async {
                                                let storyboard = UIStoryboard(name: "Main", bundle: nil)
                                                //let signUpVarification = storyboard.instantiateViewController(identifier: "SignUpVerificationVC") as! SignUpVerificationVC
                                                let signUpVarification = storyboard.instantiateViewController(withIdentifier: "SignUpVerificationVC") as! SignUpVerificationVC
                                                signUpVarification.signUpObj = value
                                                self.navigationController?.pushViewController(signUpVarification, animated: false)
                                             }
                                        case .failure(let error):
                                            DispatchQueue.main.async {
                                                self.alert(message: error.localizedDescription, title: "Exception")
                                            }
                                        }
        }
    }
}

extension SignUpVC: UITextFieldDelegate{
    func textFieldShouldReturn(_ textField: UITextField) -> Bool {
        return textField.resignFirstResponder()
    }
    
    func textFieldShouldBeginEditing(_ textField: UITextField) -> Bool {
        if textField == txtFieldState {
            guard let country = txtFieldCountry.text, !country.isEmpty else {
                alert(message: "Please select a Country", title: "Warning")
                return false
            }
        }
        return true
    }
    
    func textFieldDidEndEditing(_ textField: UITextField) {
        if textField == txtFieldCountry {
            if let selectedCountry = txtFieldCountry.pickedData as? Country {
                getStateListForCountry(selectedCountry.iso)
            }
        }
    }
    
}
