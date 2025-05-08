import type { TurboModule } from 'react-native';
interface PrinterConnectivityDtoProps {
    urn: string;
    printTest: boolean;
}
interface Spec extends TurboModule {
    connect(props: PrinterConnectivityDtoProps): Promise<void>;
}
export type { PrinterConnectivityDtoProps, Spec };
declare const _default: Spec;
export default _default;
//# sourceMappingURL=NativeZebraLinkOsPrinterConnectivity.d.ts.map