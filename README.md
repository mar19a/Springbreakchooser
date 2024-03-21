# Language-Based Destination Finder App

<img width="366" alt="Screenshot 2024-03-21 at 1 22 32 PM" src="https://github.com/mar19a/Springbreakchooser/assets/84360137/50003460-dd73-417a-bba2-4411abcf0562">


## Overview

This Android application is designed to enhance the user's language and cultural experience by integrating language selection with geographical exploration. The app features a single activity interface where users can select a language, speak a phrase, and then be guided to a vacation spot where the selected language is primarily spoken.

<img width="361" alt="Screenshot 2024-03-21 at 1 23 58 PM" src="https://github.com/mar19a/Springbreakchooser/assets/84360137/f990a52f-5051-4c54-82ad-7867f5a92263">

<img width="357" alt="Screenshot 2024-03-21 at 1 24 22 PM" src="https://github.com/mar19a/Springbreakchooser/assets/84360137/dfb0155d-4ea2-474c-b0f6-c22452164525">

## Features

- **Single Activity Interface**: The app consists of one main activity, providing a streamlined user experience.
- **Language Selection**: Users can choose from a list of languages, such as Spanish, French, and Chinese, via a dropdown box.
- **Speech to Text**: Once a language is selected, users are prompted to say a phrase. The speech input is then captured and displayed in an EditText.
- **Geographical Exploration**: By shaking the phone vigorously, users are taken to a Google Map displaying a vacation spot where the primary language corresponds to the one selected in the app.
- **Audio Greeting**: In addition to the map, an audio clip plays saying "Hello" in the selected language.

<img width="355" alt="Screenshot 2024-03-21 at 1 26 56 PM" src="https://github.com/mar19a/Springbreakchooser/assets/84360137/5c677994-a11c-4781-9fb8-16e276ddb429">

## Usage

1. **Start the App**: Open the app on your Android device.
2. **Select a Language**: Tap on the dropdown box and choose your desired language.
3. **Speak a Phrase**: When prompted, say a phrase in the selected language. The text will appear in the EditText.
4. **Shake to Discover**: Shake your device vigorously to be taken to a map displaying a relevant vacation spot.
5. **Listen**: Pay attention to the greeting played in the selected language.

<img width="220" alt="Screenshot 2024-03-21 at 1 32 47 PM" src="https://github.com/mar19a/Springbreakchooser/assets/84360137/e9b13922-05ba-46ac-be55-495a4c613227">

<img width="221" alt="Screenshot 2024-03-21 at 1 33 18 PM" src="https://github.com/mar19a/Springbreakchooser/assets/84360137/78625706-cf54-4d53-ba0f-8739f84c11d3">


## Implementation Details

- The app uses Android's Speech to Text API to convert user speech into text.
- The language selection influences the Speech to Text API to understand the user's language.
- Shaking the device triggers an intent to open a map location via a 'geo:' URI, which is predetermined based on the selected language.
- Audio playback utilizes Android's MediaPlayer to play a greeting in the selected language.

## Future Enhancements

- Expand the list of available languages.
- Allow users to select specific regions within a language-speaking country.
- Improve shake detection to prevent accidental triggers.
- Customize the map experience with additional information about the selected destination.

## Contributions

We welcome contributions from the community. If you have suggestions or want to contribute to the code, please feel free to fork the repository and submit a pull request.

## License

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE) file for details.
