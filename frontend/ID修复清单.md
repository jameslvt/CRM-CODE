# ID精度丢失修复清单

## 📋 需要修复的API文件

### 1. customer.ts
需要将以下函数的ID参数从 `number` 改为 `string`:

```typescript
// 第20行
export const getCustomerById = async (id: string): Promise<Customer> =>

// 第28行  
export const getCustomer360 = async (id: string): Promise<Customer360> =>

// 第36-37行 - 返回值
export const saveCustomer = async (data: CustomerFormData): Promise<string> => {
  const result = await request.post<string>('/business/customer', data)

// 第51行
export const deleteCustomer = async (id: string): Promise<void> =>

// 第58行
export const releaseToPool = async (id: string, reason?: string): Promise<void> =>

// 第65行
export const acquireFromPool = async (id: string): Promise<void> =>

// 第72行
export const assignCustomer = async (id: string, ownerId: string): Promise<void> =>
```

---

### 2. opportunity.ts
需要将以下函数的ID参数从 `number` 改为 `string`:

```typescript
// 第23行
export async function getOpportunitiesByCustomerId(customerId: string): Promise<Opportunity[]>

// 第31行
export async function getOpportunityById(id: string): Promise<Opportunity>

// 第39-40行 - 返回值
export async function createOpportunity(data: OpportunityFormData): Promise<string> {
  const result = await request.post<string>('/business/opportunity', data)

// 第47行
export async function updateOpportunity(id: string, data: OpportunityFormData): Promise<void>

// 第54行
export async function deleteOpportunity(id: string): Promise<void>

// 第68行
export async function getOpportunityProducts(opportunityId: string): Promise<OpportunityProduct[]>

// 第76行
export async function getOpportunityStatistics(ownerId?: string): Promise<OpportunityStatistics>
```

---

### 3. contract.ts, activity.ts, payment.ts, product.ts, contact.ts
类似地修改所有ID参数从 `number` 改为 `string`

---

## 🔍 快速修复方法

### 方法1: 全局搜索替换(推荐)

在VS Code中:

1. **搜索**: `(id: string)` 
2. **替换为**: `(id: string)`
3. **搜索**: `(customerId: string)`
4. **替换为**: `(customerId: string)`
5. **搜索**: `(ownerId: string)`
6. **替换为**: `(ownerId: string)`
7. **搜索**: `Promise<string>`
8. **替换为**: `Promise<string>`
9. **搜索**: `request.post<number>`
10. **替换为**: `request.post<string>`

**范围**: 限定在 `frontend/src/api/business/` 目录

---

### 方法2: 正则表达式替换

**搜索正则**:
```
\((\w+Id): number\)
```

**替换为**:
```
($1: string)
```

---

## ✅ 验证清单

修复完成后,确保:

- [ ] 所有API函数的ID参数都是 `string` 类型
- [ ] 所有返回ID的函数返回值都是 `Promise<string>`
- [ ] 所有 `Number(route.params.id)` 都改为 `String(route.params.id)`
- [ ] TypeScript编译无错误
- [ ] 前端请求URL中ID完整(末尾不是000)
