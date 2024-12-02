#!/bin/bash

set -e

install_helm_charts() {
  local chart_dir=$1
  echo "Устанавливаю Helm-чарты из директории: $chart_dir"

  for chart in "$chart_dir"/*; do
    if [ -d "$chart" ]; then
      chart_name=$(basename "$chart")
      echo "Устанавливаю чарт: $chart_name"
      helm upgrade --install "$chart_name" "$chart" --namespace default --create-namespace
    fi
  done
}

if [ -f "./k8s/app-config.yaml" ]; then
  echo "Применяю файл app-config.yaml"
  kubectl apply -f ./k8s/app-config.yaml
else
  echo "Файл app-config.yaml не найден в папке k8s!"
fi

if [ -d "./k8s" ]; then
  install_helm_charts "./k8s"
else
  echo "Папка k8s не найдена!"
fi

echo "Скрипт завершён успешно!"
