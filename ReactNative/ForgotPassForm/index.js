import React from 'react';
import {View, Text, TouchableOpacity} from 'react-native';
import PropTypes from 'prop-types';
import {TextInput} from 'react-native-paper';
import {useNavigation} from '@react-navigation/native';
import {useHeaderHeight} from '@react-navigation/stack';
import StaticVariables from '../../../preference/StaticVariables';
import {Colors} from '../../../theme/Colors';
import styles from './style';
import I18n from '../../../handler/Language';

const ForgotPassForm = (props) => {
  const {email, onChangeEmail, resetPassword} = props;
  const navigation = useNavigation();
  const headerHeight = useHeaderHeight();
  const marginHeader = {
    marginTop: headerHeight,
  };

  return (
    <View style={[styles.outerContainer, marginHeader]}>
      <View style={styles.textContainer}>
        <Text style={styles.titleText} >
          {I18n.t('SafeTappV2_ForgotPassword.lblEnterEmailSentence')}
        </Text>
      </View>
      <View style={styles.formContainer}>
        <TextInput
          mode="outlined"
          label={I18n.t('SafeTappV2_ForgotPassword.lblEmailPlaceholder')}
          value={email}
          style={styles.textInputContainer}
          theme={{
            colors: {
              background: Colors.name.redWhite,
              primary: Colors.name.darkGrey,
              text: Colors.name.black,
              placeholder: Colors.name.borderGrey,
            },
          }}
          onChangeText={(text) => onChangeEmail(text)}
        />
        <TouchableOpacity
          style={styles.buttonContainer}
          onPress={() => resetPassword()}>
          <Text style={styles.buttonText}>
            {I18n.t('SafeTappV2_ForgotPassword.lblSubmitButton')}
          </Text>
        </TouchableOpacity>
      </View>
      <View style={styles.bottomContainer}>
        <TouchableOpacity
          onPress={() => navigation.navigate(StaticVariables.PAGE_HELP)}>
          <Text style={styles.bottonText}>
          {I18n.t('SafeTappV2_ForgotPassword.lblStillNeedHelpButton')}
          </Text>
        </TouchableOpacity>
      </View>
    </View>
  );
};

// Prop type validation
ForgotPassForm.propTypes = {
  email: PropTypes.string.isRequired,
  onChangeEmail: PropTypes.func.isRequired,
  resetPassword: PropTypes.func.isRequired,
};

export default ForgotPassForm;
