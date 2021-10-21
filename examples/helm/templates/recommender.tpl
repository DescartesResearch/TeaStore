{{- define "teastore.recommender.microservice" -}}
recommender
{{- end }}

{{- define "teastore.recommender.fullname" -}}
{{- include "teastore.fullname" . }}-{{- include "teastore.recommender.microservice" . }}
{{- end }}

{{/*
Selector labels
*/}}
{{- define "teastore.recommender.selectorLabels" -}}
{{ include "teastore.selectorLabels" . }}
app.kubernetes.io/name: {{ include "teastore.recommender.microservice" . }}
app.kubernetes.io/component: recommender
{{- end }}

{{- define "teastore.recommender.hostname" -}}
{{- if .Values.clientside_loadbalancer -}}
$(POD_NAME).{{- .Values.recommender.svc_name -}}
{{- else if .Values.recommender.url -}}
{{- .Values.recommender.url -}}
{{- else -}}
{{- .Values.recommender.svc_name -}}
{{- end -}}
{{- end -}}
