export type Role = 'CLAIMANT' | 'CLAIMS_OFFICER' | 'CLAIMS_MANAGER';
export interface User { id: string; email: string; fullName: string; role: Role; }
