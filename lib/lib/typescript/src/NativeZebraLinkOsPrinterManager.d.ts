import type { TurboModule } from 'react-native';
interface PrintJobProps {
    id: string;
    address?: string;
    content: string;
    count: number;
    printLanguage: string;
}
interface PrinterManagerDtoProps {
    jobs: {
        [key: string]: PrintJobProps;
    };
    defaultAddresses: string[];
}
interface Spec extends TurboModule {
    print(props: PrinterManagerDtoProps): Promise<{
        [key: string]: boolean;
    }>;
}
export type { PrinterManagerDtoProps, PrintJobProps, Spec };
declare const _default: Spec;
export default _default;
//# sourceMappingURL=NativeZebraLinkOsPrinterManager.d.ts.map