import type { TurboModule } from 'react-native';
import { TurboModuleRegistry } from 'react-native';

interface PrinterConnectivityDtoProps {
  urn: string;
  printTest: boolean;
}

interface Spec extends TurboModule {
  connect(props: PrinterConnectivityDtoProps): Promise<void>;
}

export type { PrinterConnectivityDtoProps, Spec };

export default TurboModuleRegistry.getEnforcing<Spec>(
  'ZebraLinkOsPrinterConnectivity'
);
