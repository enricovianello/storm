/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

#ifndef STORM_LCMAPS_H_
#define STORM_LCMAPS_H_

#ifdef __cplusplus
extern "C" {
#endif

int init_lcmaps();
int map_user(const char *user_dn, const char **fqan_list, int nfqan, int *uid, void **gids, int *ngids);
void free_gids(void **p);

#ifdef __cplusplus
}
#endif

#endif /* STORM_LCMAPS_H_ */

