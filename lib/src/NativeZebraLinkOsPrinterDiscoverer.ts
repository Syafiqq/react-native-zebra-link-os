import type { TurboModule } from 'react-native';
import { TurboModuleRegistry } from 'react-native';

interface PrinterDiscovererDtoProps {
  bluetooth?: boolean;
  bluetoothLE?: boolean;
  localBroadcast?: boolean;
  directIpAddress?: string;
  multicastHops?: string;
  subnetRange?: string;
  nearby?: boolean;
}

interface PrinterDiscovererResponseProps {
  name?: string;
  address?: string;
  urn?: string;
  type?: string;
}

interface Spec extends TurboModule {
  discover(
    props: PrinterDiscovererDtoProps
  ): Promise<PrinterDiscovererResponseProps[]>;
}

export type { PrinterDiscovererDtoProps, PrinterDiscovererResponseProps, Spec };

export default TurboModuleRegistry.getEnforcing<Spec>(
  'ZebraLinkOsPrinterDiscoverer'
);
