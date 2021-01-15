import {StyleSheet} from 'react-native';
import {Colors} from '../../../theme/Colors';
import {Dimensions} from '../../../theme/Dimensions';

const styles = StyleSheet.create({
  outerContainer: {
    padding: Dimensions.DeviceWidth / 20,
    paddingBottom:Dimensions.DeviceWidth / 8,
    flex: 1,
  },
  textContainer: {
    flex: 1,
    justifyContent: 'flex-end',
  },
  formContainer: {
    flex: 2,
    paddingTop: Dimensions.DeviceHeight / 20,
  },
  inputText: {
    height: Dimensions.DeviceWidth / 8,
    borderColor: Colors.name.lightGrey,
    borderWidth: 1,
    marginTop: 10,
    width: '100%',
    backgroundColor: '#fbf9fa',
    padding: 10,
  },
  buttonContainer: {
    marginTop: 20,
    marginBottom: 20,
    height: Dimensions.DeviceHeight / 14,
    flexDirection: 'row',
    justifyContent: 'center',
    alignItems: 'center',
    width: '100%',
    borderRadius: Dimensions.DeviceWidth / 60,
    backgroundColor: Colors.name.lightGreen,
  },
  buttonText: {
    color: Colors.theme.backgroundPrimary,
    fontSize: Dimensions.DeviceHeight / 40,
    fontWeight: 'bold',
  },
  bottomContainer: {
    flex: 1,
    justifyContent: 'flex-end',
    alignItems: 'center',
  },
  bottonText: {
    color: Colors.theme.appPrimary,
    fontSize:Dimensions.DeviceHeight / 50
  },
  textInputContainer: {
    fontSize: Dimensions.DeviceHeight / 52,
    height: Dimensions.DeviceHeight / 14
  },
  titleText: {
    fontSize: Dimensions.DeviceHeight / 52,
  },
});

export default styles;
