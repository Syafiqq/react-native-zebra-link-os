import { Button, StyleSheet, View } from 'react-native';
import {
  PrinterConnectivity,
  PrinterDiscoverer,
  PrinterManager,
} from 'react-native-zebra-link-os';

export default function App() {
  const buttonPress = () => {
    PrinterDiscoverer.discover({
      bluetooth: true,
    })
      .then((r) =>
        PrinterConnectivity.connect({
          urn: r[0]?.urn ?? '',
          printTest: true,
        })
          .then(() =>
            PrinterConnectivity.connect({
              urn: r[1]?.urn ?? '',
              printTest: true,
            })
          )
          .then(() => r)
      )
      .then((r) =>
        PrinterManager.print({
          jobs: {
            '1': {
              id: '1',
              address: r[0]?.urn ?? '',
              content:
                '^XA\r\n' +
                '^FO10,10\r\n' +
                '^A0N,20,20\r\n' +
                '^FDType: Testing^FS\r\n' +
                '^XZ\r\n',
              count: 1,
              printLanguage: 'ZPL',
            },
            '2': {
              id: '2',
              address: r[1]?.urn ?? '',
              content:
                '! 0 200 200 350 1\r\n' +
                'TEXT 0 2 50 100 Testing\r\n' +
                'FORM\r\n' +
                'PRINT\r\n',
              count: 1,
              printLanguage: 'CPCL',
            },
          },
          defaultAddresses: r.map((k) => k.urn ?? '') ?? [],
        }).then()
      )
      .then(() => {
        console.log('Print job sent successfully');
      })
      .catch((e) => {
        console.error('Error connecting to printer:', e);
      });
  };

  return (
    <View style={styles.container}>
      <Button onPress={buttonPress} title="Discover+Connect+Print" />
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
    backgroundColor: 'red',
  },
});
